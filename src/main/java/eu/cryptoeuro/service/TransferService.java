package eu.cryptoeuro.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.DatatypeConverter;

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

    private AccountService accountService;
    private EmailService emailService;
    private ContractConfig contractConfig;
    private KeyUtil keyUtil;

    @Autowired
    public TransferService(ContractConfig contractConfig, AccountService accountService, EmailService emailService, KeyUtil keyUtil) {
        this.contractConfig = contractConfig;
        this.accountService = accountService;
        this.emailService = emailService;
        this.keyUtil = keyUtil;
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

    public Transfer delegatedBankTransfer(CreateBankTransferCommand bankTransfer){
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
		log.info("Sending  email instructions for bank transfer "+result.getId());
		//instructions by email to send out bank transfer	
		
		String emailText = new String();

		emailText = "bank account: "+bankTransfer.getTargetBankAccountIBAN()+
		"\n amount: "+bankTransfer.getAmount()+
		"\n reference: "+bankTransfer.getReference()+
		"\n from: "+bankTransfer.getSourceAccount()+
		"\n txhash: "+result.getId()+
		"\n name: "+bankTransfer.getRecipientName();

		log.info("Email should go with body: "+emailText);

		emailService.sendEmail(this.bankProxyInstructionEmail, "Euro2.0 bank payout", emailText );
	}

	return result;
    }

    // TODO cleanup
    public Iterable<Transfer> getAll(){
        return null;
    }

    /*
    public String mintToken(Optional<String> account, Optional<Long> amount) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_sendtransaction
        Map<String, String> params = new HashMap<>();
        params.put("from", "0x4FfAaD6B04794a5911E2d4a4f7F5CcCEd0420291"); // erko main account
        params.put("to", "0xAF8ce136A244dB6f13a97e157AC39169F4E9E445"); // viimane 0.21 contract deploy
        //params.put("gas", "0x76c0"); // 30400, 21000
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "");

        String targetArgument = "000000000000000000000000" + account.orElse("0x52C312631d5593D9164A257abcD5c58d14B96600").substring(2); // erko wallet contract aadress
        // TODO use amount variable
        String amountArgument = "0000000000000000000000000000000000000000000000000000000000000065"; // 101 raha
        String data = "0x" + HashUtils.keccak256("mintToken(address,uint256)").substring(0, 8) + targetArgument + amountArgument;

        params.put("data", data);
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.sendTransaction, Arrays.asList(params));

        log.info("JSON:\n"+call.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);
        log.info("Sending request to: " + URL);
        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);

        log.info("Send transaction response: " + response.getResult());

        / *
        byte[] bytes = DatatypeConverter.parseHexBinary(response.getResult().substring(2));
        String convertedResult = new String(bytes, StandardCharsets.UTF_8);
        log.info("Converted to string: " + convertedResult);

        long longResult = Long.parseLong(response.getResult().substring(2), 16);
        log.info("Converted to long: " + longResult);
        * /

        return response.getResult();
    }
    */

    public List<Transfer> getTransfersForAccount(String address) {
        return Stream.concat(
		getTransfersForAccountFromTo(null, address).stream(),
		getTransfersForAccountFromTo(address,null).stream()
	     ).collect(Collectors.toList());
    }

    public Transfer get(String transactionHash) {

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.getTransactionByHash, Arrays.asList(transactionHash));

        JsonRpcTransactionResponse response = getCallResponseForObject(call, JsonRpcTransactionResponse.class);
	
        log.info("Is "+transactionHash+" mined yet? " + response.isMined());
        Transfer transfer = new Transfer();
        transfer.setStatus((response.isMined()) ? TransferStatus.SUCCESSFUL : TransferStatus.PENDING);
        transfer.setId(transactionHash);
        transfer.setBlockHash(response.getBlockHash());
	return transfer;
    }


    ///// PRIVATE METHODS /////

    private List<Transfer> getTransfersForAccountFromTo(String fromAddress, String toAddress) {
        String transferMethodSignatureHash = "0x" + HashUtils.keccak256("Transfer(address,address,uint256)");
        String paddedFromAddress = (fromAddress != null) ? HashUtils.padAddressTo64(fromAddress) : null;
        String paddedToAddress = (toAddress != null) ? HashUtils.padAddressTo64(toAddress) : null;

        List<String> topicsToFind = new ArrayList<>();
        topicsToFind.add(transferMethodSignatureHash);
        topicsToFind.add(paddedFromAddress);
        topicsToFind.add(paddedToAddress);

        Map<String, Object> params = new HashMap<>();
        params.put("address", contractConfig.getAllContracts());
        params.put("fromBlock", CONTRACT_FROM_BLOCK);
        params.put("topics", topicsToFind);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.getLogs, Arrays.asList(params));

        JsonRpcTransactionLogResponse response = getCallResponseForObject(call, JsonRpcTransactionLogResponse.class);

        Map<String, Long> fees = new HashMap<>();

        log.info(response.getResult().toString());
        return response.getResult().stream().map(logEntry -> {
	    String toAddr = new String(HashUtils.unpadAddress(logEntry.getTopics().get(2)));

	    if (this.feeReceiverAddresses.contains(toAddr)) {
		fees.put(logEntry.getTransactionHash(),Long.parseLong(logEntry.getData().substring(2), 16));
	        return null;
	    }

            Transfer transfer = new Transfer();

            transfer.setId(logEntry.getTransactionHash());
            transfer.setBlockHash(logEntry.getBlockHash()); // todo check if corect
            transfer.setSourceAccount(HashUtils.unpadAddress(logEntry.getTopics().get(1)));
            transfer.setTargetAccount(HashUtils.unpadAddress(logEntry.getTopics().get(2)));
            transfer.setAmount(Long.parseLong(logEntry.getData().substring(2), 16));

            JsonRpcCallMap blockCall = new JsonRpcCallMap(EthereumRpcMethod.getBlockByHash, Arrays.asList(logEntry.getBlockHash(),false));

            JsonRpcBlockResponse blockResponse = getCallResponseForObject(blockCall, JsonRpcBlockResponse.class);
            transfer.setTimestamp(blockResponse.getTimestamp());

            return transfer;
        } ).filter(l -> l != null).collect(Collectors.toList()).stream().map( tx -> {

	    tx.setFee(fees.get(tx.getId()));
	    return tx;

	} ).collect(Collectors.toList());
    }

    private void checkSourceAccountApproved(String account) {
        if(!accountService.isApproved(account)) {
            throw new AccountNotApprovedException();
        }
    }

    private String sendRawTransaction(ECKey signer, byte[] callData) {
        long transactionCount = getTransactionCount(SPONSOR);
        byte[] nonce = ByteUtil.longToBytesNoLeadZeroes(transactionCount);
        long gasPriceLong = Math.round(getGasPrice() * 1.1); // KK: not sure if increasing gas price by 10% is necessary
        byte[] gasPrice = ByteUtil.longToBytesNoLeadZeroes(gasPriceLong);
        byte[] gasLimit = ByteUtil.longToBytesNoLeadZeroes(350000);
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


    private long getGasPrice() {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.gasPrice, Arrays.asList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);
        long responseToLong = Long.parseLong(HashUtils.without0x(response.getResult()), 16);
        log.info("Gas price: " + responseToLong);

        return responseToLong;
    }
}
