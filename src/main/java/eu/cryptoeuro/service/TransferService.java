package eu.cryptoeuro.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

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

import eu.cryptoeuro.FeeConstant;
import eu.cryptoeuro.rest.command.CreateTransferCommand;
import eu.cryptoeuro.rest.model.Transfer;
import eu.cryptoeuro.rest.model.TransferStatus;
import eu.cryptoeuro.service.exception.AccountNotApprovedException;
import eu.cryptoeuro.service.exception.FeeMismatchException;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import eu.cryptoeuro.service.rpc.JsonRpcTransactionLogResponse;

@Component
@Slf4j
public class TransferService extends BaseService {

    @Autowired
    private AccountService accountService;

    // CONTRACT function: delegatedTransfer(uint256 nonce, address destination, uint256 amount, uint256 fee, bytes signature, address delegate)
    private static Function delegatedTransferFunction = Function.fromSignature("delegatedTransfer", "uint256", "address", "uint256", "uint256", "bytes", "address");

    public Transfer delegatedTransfer(CreateTransferCommand transfer){
        checkSourceAccountApproved(transfer.getSourceAccount());
        checkSourceAccountApproved(transfer.getTargetAccount());
        //TODO check that Target account not closed
        //TODO check that Source has sufficient EUR balance (for amount + fee)
        //TODO check that we have enough ETH to submit
        //TODO currently "reference" field is ignored, what to do with that?

        //TODO that's better to validate in TransferCommand, no ?
        if (FeeConstant.FEE.compareTo(transfer.getFee()) != 0) {
            throw new FeeMismatchException(transfer.getFee(), FeeConstant.FEE);
        }

        ECKey sponsorKey = getWalletServerSponsorKey();
        byte[] signatureArg = DatatypeConverter.parseHexBinary(transfer.getSignature());
        byte[] callData = delegatedTransferFunction.encode(transfer.getNonce(), transfer.getTargetAccount(), transfer.getAmount(), transfer.getFee(), signatureArg, SPONSOR);

        String txHash = sendRawTransaction(sponsorKey, callData);

        Transfer result = new Transfer();
        result.setId(txHash);
        result.setStatus(TransferStatus.PENDING);
        result.setAmount(transfer.getAmount());
        result.setTargetAccount(transfer.getTargetAccount());
        result.setSourceAccount(transfer.getSourceAccount());
        result.setFee(transfer.getFee());
        result.setNonce(transfer.getNonce());
        result.setReference(transfer.getReference());
        result.setSignature(transfer.getSignature());
        return result;
    }

    /*
    public Transfer delegatedTransfer041(CreateTransferCommand transfer){
        checkSourceAccountApproved(transfer.getSourceAccount());
    	//TODO: check that Target account not closed
    	//TODO: check that Source has sufficient EUR balance
    	//TODO: check nonce is high enough, probably good to return nonce in /accounts/0x123...
    	//TODO: check that we have enough ETH to submit
        //TODO: that's better to validate in TransferCommand, no ?
        if (FeeConstant.FEE.compareTo(transfer.getFee()) != 0) {
            throw new FeeMismatchException(transfer.getFee(), FeeConstant.FEE);
        }

        String from = HashUtils.padAddress(transfer.getSourceAccount());
        String to = HashUtils.padAddress(transfer.getTargetAccount());
        String amount = HashUtils.padLong(transfer.getAmount());
        String fee = HashUtils.padLong(transfer.getFee());
        String nonce = HashUtils.padLong(transfer.getNonce());
        String v =  HashUtils.padLong(transfer.getSigV());
        String r = transfer.getSigR().substring(2);
        String s = transfer.getSigS().substring(2);
        String sponsor = HashUtils.padAddress(SPONSOR);
        String data = "0x"
                + HashUtils.keccak256("delegatedTransfer(address,address,uint256,uint256,uint256,uint8,bytes32,bytes32,address)").substring(0, 8)
                + from
                + to
                + amount
                + fee
                + nonce
                + v
                + r
                + s
                + sponsor;

        Map<String, String> params = new HashMap<>();
        params.put("from", SPONSOR);
        params.put("to", CONTRACT);
        params.put("gas", "0x13880"); // 80000
        params.put("gasPrice", "0x4A817C800"); // 20000000000
        params.put("data", data);
        //params.put("value", "");
        //params.put("nonce", "1"); // TODO: changeme

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.sendTransaction, Arrays.asList(params));
        log.info("JSON:\n" + call.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(URL, request, JsonRpcStringResponse.class);

        log.info("Received transaction response: " + response.getResult());

        Transfer result = new Transfer();
        result.setId(response.getResult());
        result.setStatus(TransferStatus.PENDING);
        result.setAmount(transfer.getAmount());
        result.setTargetAccount(transfer.getTargetAccount());
        result.setSourceAccount(transfer.getSourceAccount());
        result.setFee(transfer.getFee());
        result.setNonce(transfer.getNonce());
        result.setReference(transfer.getReference());
        result.setSigV(transfer.getSigV());
        result.setSigR(transfer.getSigR());
        result.setSigS(transfer.getSigS());
        return result;
    }
    */

    // TODO cleanup
    public Transfer get(Long id){
        return null;
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
        String transferMethodSignatureHash = "0x" + HashUtils.keccak256("Transfer(address,address,uint256)");
        String paddedAddress = HashUtils.padAddress(address);

        List<String> topicsToFind = new ArrayList<>();
        topicsToFind.add(transferMethodSignatureHash);
        topicsToFind.add(paddedAddress);

        Map<String, Object> params = new HashMap<>();
        params.put("address", CONTRACT);
        params.put("fromBlock", CONTRACT_FROM_BLOCK);
        params.put("topics", topicsToFind);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.getLogs, Arrays.asList(params));

        JsonRpcTransactionLogResponse response = getCallResponseForObject(call, JsonRpcTransactionLogResponse.class);

        //todo filter delegated transfers which have fee and those that don't
        log.info(response.getResult().toString());
        return response.getResult().stream().map(logEntry -> {
            Transfer transfer = new Transfer();

            transfer.setId(logEntry.getBlockHash());
            transfer.setSourceAccount(HashUtils.unpadAddress(logEntry.getTopics().get(1)));
            transfer.setTargetAccount(HashUtils.unpadAddress(logEntry.getTopics().get(2)));
            transfer.setAmount(Long.parseLong(logEntry.getData().substring(2), 16));

            return transfer;
        } ).collect(Collectors.toList());
    }

    ///// PRIVATE METHODS /////

    private static String hex(byte[] bytes) {
        return with0x(Hex.toHexString(bytes));
    }

    private static String without0x(String hex) {
        return hex.startsWith("0x") ? hex.substring(2) : hex;
    }

    private static String with0x(String hex) {
        return hex.startsWith("0x") ? hex : "0x" + hex;
    }

    private static ECKey getWalletServerSponsorKey() {
        File file = new File(System.getProperty("user.home"), ".WalletServerSponsor.key");
        try {
            String keyHex = toString(new FileInputStream(file));
            return ECKey.fromPrivate(Hex.decode(keyHex));
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot load wallet-server sponsor account key. Make sure " + file.toString() + " exists and contains the private key in hex format.\n" + e.toString());
        }
    }

    private static String toString(InputStream stream) throws IOException {
        try (InputStream is = stream) {
            return new Scanner(is).useDelimiter("\\A").next();
        }
    }

    private void checkSourceAccountApproved(String account) {
        if(!accountService.isApproved(account)) {
            throw new AccountNotApprovedException();
        }
    }

    private String sendRawTransaction(ECKey signer, byte[] callData) {
        long transactionCount = getTransactionCount(SPONSOR);
        byte[] nonce = ByteUtil.longToBytesNoLeadZeroes(transactionCount);
        byte[] gasPrice = ByteUtil.longToBytesNoLeadZeroes(20000000000L);
        byte[] gasLimit = ByteUtil.longToBytesNoLeadZeroes(100000);
        byte[] toAddress = Hex.decode(without0x(CONTRACT));

        Transaction transaction = new Transaction(nonce, gasPrice, gasLimit, toAddress, null, callData);
        transaction.sign(signer.getPrivKeyBytes());
        String params = hex(transaction.getEncoded());

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
        long responseToLong = Long.parseLong(without0x(response.getResult()), 16);
        log.info("Transaction count for " + account + ": " + responseToLong);

        return responseToLong;
    }

}
