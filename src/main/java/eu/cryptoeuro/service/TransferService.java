package eu.cryptoeuro.service;

import java.util.*;

import eu.cryptoeuro.service.rpc.JsonRpcListResponse;
import lombok.extern.slf4j.Slf4j;

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
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class TransferService extends BaseService {

    @Autowired
    private AccountService accountService;

    public Transfer delegatedTransfer(CreateTransferCommand transfer){

        checkSourceAccountApproved(transfer.getSourceAccount());
    	//TODO: check that Target account not closed
    	//TODO: check that Source has sufficient EUR balance
    	//TODO: check nonce is high enough, probably good to return nonce in /accounts/0x123...
    	//TODO: check that we have enough ETH to submit

        //TODO: that's better to validate in TransferCommand, no ?
        if (FeeConstant.FEE.compareTo(transfer.getFee()) != 0) {
            throw new FeeMismatchException(transfer.getFee(),FeeConstant.FEE);
        }

        String from = String.format("%64s", transfer.getSourceAccount().substring(2)).replace(" ", "0");
        String to = String.format("%64s", transfer.getTargetAccount().substring(2)).replace(" ", "0");
        String amount = String.format("%064x", transfer.getAmount() & 0xFFFFF);
        String fee = String.format("%064x", transfer.getFee() & 0xFFFFF);
        String nonce = String.format("%064x", transfer.getNonce() & 0xFFFFF);
        String v = String.format("%064x", transfer.getSigV() & 0xFFFFF);
        String r = transfer.getSigR().substring(2);
        String s = transfer.getSigS().substring(2);
        String sponsor = String.format("%64s", SPONSOR.substring(2)).replace(" ", "0");
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

        Transfer transferResponse = new Transfer();
        transferResponse.setId(response.getResult());
        transferResponse.setStatus(TransferStatus.PENDING);
        transferResponse.setAmount(transfer.getAmount());
        transferResponse.setTargetAccount(transfer.getTargetAccount());
        transferResponse.setSourceAccount(transfer.getSourceAccount());
        transferResponse.setFee(transfer.getFee());
        transferResponse.setNonce(transfer.getNonce());
        transferResponse.setReference(transfer.getReference());
        transferResponse.setSigV(transfer.getSigV());
        transferResponse.setSigR(transfer.getSigR());
        transferResponse.setSigS(transfer.getSigS());

        return transferResponse;
    }

    public Transfer get(Long id){
        return null;
    }

    public Iterable<Transfer> getAll(){
        return null;
    }

    private void checkSourceAccountApproved(String account) {
        if(!accountService.isApproved(account)) {
            throw new AccountNotApprovedException();
        }
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

    public String getTransfersForAccount(String address) {
        String transferMethodSignatureHash = HashUtils.keccak256("Transfer(address,address,uint256)");
        String paddedAddress = String.format("%64s", address.substring(2)).replace(" ", "0");

        List<String> topicsToFind = new ArrayList<>();
        topicsToFind.add(transferMethodSignatureHash);
        topicsToFind.add(paddedAddress);

        Map<String, Object> params = new HashMap<>();
        params.put("address", CONTRACT);
        params.put("fromBlock", CONTRACT_FROM_BLOCK);
        params.put("topics", topicsToFind);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.logs, Arrays.asList(params));

        JsonRpcListResponse response = getCallResponse(call);
        //todo response for log
        log.info(response.getResult().toString());

        return response.getResult().toString();
    }

}
