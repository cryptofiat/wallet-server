package eu.cryptoeuro.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.DatatypeConverter;

import eu.cryptoeuro.rest.model.TransferHistory;
import eu.cryptoeuro.rest.model.TransferHistoryManager;
import eu.cryptoeuro.util.KeyUtil;
import lombok.extern.slf4j.Slf4j;

import org.ethereum.core.CallTransaction.Function;
import org.ethereum.core.Transaction;
import org.ethereum.crypto.ECKey;
import org.ethereum.util.ByteUtil;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.config.ContractConfig;
import eu.cryptoeuro.rest.command.CreateTransferCommand;
import eu.cryptoeuro.rest.command.CreateBankTransferCommand;
import eu.cryptoeuro.rest.model.Transfer;
import eu.cryptoeuro.rest.model.TransferStatus;
import eu.cryptoeuro.service.exception.AccountNotApprovedException;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import eu.cryptoeuro.service.rpc.JsonRpcBlockResponse;
import eu.cryptoeuro.service.rpc.JsonRpcTransactionLogResponse;
import eu.cryptoeuro.service.rpc.JsonRpcTransactionResponse;

@Component
@Slf4j
public class TransferService extends BaseService {

    public static final int GAS_LIMIT = 350000;

    private AccountService accountService;
    private EmailService emailService;
    private ContractConfig contractConfig;
    private KeyUtil keyUtil;
    private GasPriceService gasPriceService;

    @Autowired
    private SlackService slackService;

    @Autowired
    public TransferService(ContractConfig contractConfig, AccountService accountService, EmailService emailService, KeyUtil keyUtil, GasPriceService gasPriceService) {
        this.contractConfig = contractConfig;
        this.accountService = accountService;
        this.emailService = emailService;
        this.keyUtil = keyUtil;
        this.gasPriceService = gasPriceService;
    }

    // list of addresses that should be considered as recipients of fees
    private static List<String> feeReceiverAddresses = Arrays.asList("0x8664e7a68809238d8f8e78e4b7c723282533a787");

    // gateway address for sending to SEPA bank accounts
    //private static String bankProxyAddress = "0x8664e7a68809238d8f8e78e4b7c723282533a787";
    private static final String bankProxyAddress = "0x833898875a12a3d61ef18dc3d2b475c7ca3a4a72";
    private static final String bankProxyInstructionEmail = "kristoxz@yahoo.com";

    // Function definition: transfer(uint256 nonce, address destination, uint256 amount, uint256 fee, bytes signature, address delegate)
    private static Function transferFunction = Function.fromSignature("transfer", "uint256", "address", "uint256", "uint256", "bytes", "address");

    public Transfer delegatedTransfer(CreateTransferCommand transfer){
        log.info("delegatedTransfer:");
        log.info("sourceAccount:" + transfer.getSourceAccount());
        log.info("targetAccount:" + transfer.getTargetAccount());
        log.info("amount:" + transfer.getAmount());
        log.info("fee:" + transfer.getFee());
        log.info("nonce:" + transfer.getNonce());
        log.info("signature:" + transfer.getSignature());

        checkSourceAccountApproved(transfer.getSourceAccount());
        checkSourceAccountApproved(transfer.getTargetAccount());
        //TODO check that Target account not closed
        //TODO check that Source has sufficient EUR balance (for amount + fee)
        //TODO check that we have enough ETH to submit
        //TODO currently "reference" field is ignored, what to do with that?

        ECKey sponsorKey = keyUtil.getWalletServerSponsorKey();
        byte[] signatureArg = DatatypeConverter.parseHexBinary(transfer.getSignature());
        byte[] callData = transferFunction.encode(transfer.getNonce(), transfer.getTargetAccount(), transfer.getAmount(), transfer.getFee(), signatureArg, SPONSOR);

        String txHash = sendRawTransaction(sponsorKey, callData);

        Transfer result = new Transfer();
        result.setId(txHash);
        result.setStatus(TransferStatus.PENDING);
        result.setAmount(transfer.getAmount());
        result.setTargetAccount(transfer.getTargetAccount());
        result.setSourceAccount(transfer.getSourceAccount());
        result.setFee(transfer.getFee());
        result.setNonce(transfer.getNonce());
        result.setSignature(transfer.getSignature());
        return result;
    }

    public Transfer delegatedBankTransfer(CreateBankTransferCommand bankTransfer) {
        log.info("delegatedBankTransfer:");
        log.info("sourceAccount:" + bankTransfer.getSourceAccount());
        log.info("targetBankAccountIBAN:" + bankTransfer.getTargetBankAccountIBAN());
        log.info("amount:" + bankTransfer.getAmount());
        log.info("fee:" + bankTransfer.getFee());
        log.info("nonce:" + bankTransfer.getNonce());
        log.info("signature:" + bankTransfer.getSignature());

        CreateTransferCommand ethTransfer = new CreateTransferCommand();
        ethTransfer.setAmount(bankTransfer.getAmount());
        ethTransfer.setSourceAccount(bankTransfer.getSourceAccount());
        ethTransfer.setFee(bankTransfer.getFee());
        ethTransfer.setNonce(bankTransfer.getNonce());
        ethTransfer.setSignature(bankTransfer.getSignature());

        ethTransfer.setTargetAccount(this.bankProxyAddress);
        Transfer result = delegatedTransfer(ethTransfer);

        //TODO: Here should be something that forks the thread and waits async until the transfer has been mined. Or even do this part as job.

        if (result.getId() != null) {

            slackService.notifyPayout(bankTransfer, result.getId());

            log.info("Sending  email instructions for bank transfer " + result.getId());

            String emailText = "bank account: " + bankTransfer.getTargetBankAccountIBAN() +
                "\n amount: " + bankTransfer.getAmount() +
                "\n reference: " + bankTransfer.getReference() +
                "\n from: " + bankTransfer.getSourceAccount() +
                "\n txhash: " + result.getId() +
                "\n name: " + bankTransfer.getRecipientName();

            log.info("Email should go with body: " + emailText);


            emailService.sendEmail(this.bankProxyInstructionEmail, "Euro2.0 bank payout", emailText);
        }

        return result;
    }

    public List<Transfer> getTransfersForAccount(String address) {
        TransferHistory transferHistory = TransferHistoryManager.getInstance().getTransferHistory(address);
        log.info("Loaded history cache for address " + address + " up to block " + transferHistory.lastBlock);
        long latestBlock = getLatestBlock();
        transferHistory.transferList = Stream.concat(
            transferHistory.transferList.stream(),
            getTransfersForAccount(address, transferHistory.lastBlock + 1).stream() // todo: should be to latest block
        ).collect(Collectors.toList());
        transferHistory.lastBlock = latestBlock; //get new last block
        TransferHistoryManager.getInstance().storeTransferHistory(transferHistory);
        return transferHistory.transferList;
    }

    public List<Transfer> getTransfersForAccount(String address, long fromBlock) {
        return Stream.concat(
            getTransfersForAccountFromTo(null, address, fromBlock).stream(),
            getTransfersForAccountFromTo(address, null, fromBlock).stream()
        ).collect(Collectors.toList());
    }

    public Transfer get(String transactionHash) {

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.getTransactionByHash, Arrays.asList(transactionHash));

        JsonRpcTransactionResponse response = getCallResponseForObject(call, JsonRpcTransactionResponse.class);

        log.info("Is " + transactionHash + " mined yet? " + response.isMined());
        Transfer transfer = new Transfer();
        transfer.setStatus((response.isMined()) ? TransferStatus.SUCCESSFUL : TransferStatus.PENDING);
        transfer.setId(transactionHash);
        transfer.setBlockHash(response.getBlockHash());
        return transfer;
    }


    ///// PRIVATE METHODS /////

    private List<Transfer> getTransfersForAccountFromTo(String fromAddress, String toAddress, long fromBlock) {
        String transferMethodSignatureHash = "0x" + HashUtils.keccak256("Transfer(address,address,uint256)");
        String paddedFromAddress = (fromAddress != null) ? HashUtils.padAddressTo64(fromAddress) : null;
        String paddedToAddress = (toAddress != null) ? HashUtils.padAddressTo64(toAddress) : null;

        List<String> topicsToFind = new ArrayList<>();
        topicsToFind.add(transferMethodSignatureHash);
        topicsToFind.add(paddedFromAddress);
        topicsToFind.add(paddedToAddress);

        Map<String, Object> params = new HashMap<>();
        params.put("address", contractConfig.getAllContracts());
        params.put("fromBlock", (Long.decode(CONTRACT_FROM_BLOCK) > fromBlock) ? CONTRACT_FROM_BLOCK : "0x" + Long.toHexString(fromBlock));
        params.put("topics", topicsToFind);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.getLogs, Arrays.asList(params));

        JsonRpcTransactionLogResponse response = getCallResponseForObject(call, JsonRpcTransactionLogResponse.class);

        if (response.hasError()) {
            log.error("Error getting Transaction Logs: " + response.getError().getMessage());
        }

        Map<String, Long> fees = new HashMap<>();

        log.info(response.getResult().toString());
        return response.getResult().stream().map(logEntry -> {
            String toAddr = new String(HashUtils.unpadAddress(logEntry.getTopics().get(2)));

            if (this.feeReceiverAddresses.contains(toAddr)) {
                fees.put(logEntry.getTransactionHash(), Long.parseLong(logEntry.getData().substring(2), 16));
                return null;
            }

            Transfer transfer = new Transfer();

            transfer.setId(logEntry.getTransactionHash());
            transfer.setBlockHash(logEntry.getBlockHash()); // todo check if corect
            transfer.setSourceAccount(HashUtils.unpadAddress(logEntry.getTopics().get(1)));
            transfer.setTargetAccount(HashUtils.unpadAddress(logEntry.getTopics().get(2)));
            transfer.setAmount(Long.parseLong(logEntry.getData().substring(2), 16));

            JsonRpcCallMap blockCall = new JsonRpcCallMap(EthereumRpcMethod.getBlockByHash, Arrays.asList(logEntry.getBlockHash(), false));

            JsonRpcBlockResponse blockResponse = getCallResponseForObject(blockCall, JsonRpcBlockResponse.class);
            transfer.setTimestamp(blockResponse.getTimestamp());

            return transfer;
        }).filter(l -> l != null).collect(Collectors.toList()).stream().map(tx -> {

            tx.setFee(fees.get(tx.getId()));
            return tx;

        }).collect(Collectors.toList());
    }

    private void checkSourceAccountApproved(String account) {
        if (!accountService.isApproved(account)) {
            throw new AccountNotApprovedException();
        }
    }

    private String sendRawTransaction(ECKey signer, byte[] callData) {
        long transactionCount = getTransactionCount(SPONSOR);
        byte[] nonce = ByteUtil.longToBytesNoLeadZeroes(transactionCount);
        long gasPriceWei = gasPriceService.getGasPriceInWei();
        byte[] gasPrice = ByteUtil.longToBytesNoLeadZeroes(gasPriceWei);
        byte[] gasLimit = ByteUtil.longToBytesNoLeadZeroes(GAS_LIMIT);
        byte[] toAddress = Hex.decode(HashUtils.without0x(contractConfig.getDelegationContractAddress()));

        Transaction transaction = new Transaction(nonce, gasPrice, gasLimit, toAddress, null, callData);
        transaction.sign(signer.getPrivKeyBytes());
        String params = HashUtils.hex(transaction.getEncoded());

        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.sendRawTransaction, Arrays.asList(params));
        log.info("JSON:\n" + call.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        String txHash = response.getResult();
        log.info("Received transaction response: " + txHash);

        return txHash;
    }

    private long getTransactionCount(String account) {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.getTransactionCount, Arrays.asList(account, "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        long responseToLong = Long.parseLong(HashUtils.without0x(response.getResult()), 16);
        log.info("Transaction count for " + account + ": " + responseToLong);

        return responseToLong;
    }

    private long getLatestBlock() {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.blockNumber, Arrays.asList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        long responseToLong = Long.parseLong(HashUtils.without0x(response.getResult()), 16);
        log.info("Latest block: " + responseToLong);

        return responseToLong;
    }
}
