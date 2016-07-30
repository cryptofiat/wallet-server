package eu.cryptoeuro.service;

import eu.cryptoeuro.dao.TransferRepository;
import eu.cryptoeuro.domain.Transfer;
import eu.cryptoeuro.domain.TransferStatus;
import eu.cryptoeuro.rest.command.CreateTransferCommand;
import eu.cryptoeuro.rest.model.Fee;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TransferService extends BaseService {

    @Autowired
    TransferRepository transferRepository;

    public final String SPONSOR = "0x65fa6548764C08C0DD77495B33ED302d0C212691";
    public final String CONTRACT = "0x640Da14959D6A6244f35471080BEBd960F15FDAe`"; //0.31

    public Transfer save(CreateTransferCommand transfer){
//        transfer.setStatus(TransferStatus.PENDING);
//        transfer = transferRepository.save(transfer);
//        final Long transferId = transfer.getId();

        Map<String, String> params = new HashMap<>();
        params.put("from", SPONSOR);
        params.put("to", CONTRACT);
        params.put("nounce", "1"); // TODO: changeme
        params.put("gas", "0x249F0"); // 150000
        params.put("gasPrice", "0x4A817C800"); // 20000000000
        //params.put("value", "");

        String from = String.format("%064d", transfer.getSourceAccount().substring(2));
        String to = String.format("%064d", transfer.getTargetAccount().substring(2));
        String amount = String.format("%064X", transfer.getAmount() & 0xFFFFF);

        String reference = null;
        if(transfer.getReference().isPresent()) {
            reference =
                    String.format("%064X", transfer.getReference().orElse(null) & 0xFFFFF);
        }

        String fee = String.format("%064X", Fee.amount & 0xFFFFF);
        String nounce = String.format("%064X", 0 & 0xFFFFF);

        String sponsor = String.format("%064d", SPONSOR.substring(2));

        String v = "";
        String r = "";
        String s = "";

        String data = "0x" + HashUtils.keccak256(
                "signedTransfer(address,address,uint256,uint256,uint256,uint256,\n" +
                "address,\n" +
                "uint8,bytes32,bytes32)").substring(0, 8)
                + from + to + amount + reference + fee +
                nounce + sponsor +
                v + r + s;

        params.put("data", data);
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.sendTransaction, Arrays.asList(params));

        log.info("JSON:\n"+call.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);
        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        log.info("Send transaction response: " + response.getResult());

        response.getResult();




        return null;
    }

    public Transfer get(Long id){
        return transferRepository.findOne(id);
    }

    public Iterable<Transfer> getAll(){
        return transferRepository.findAll();
    }
/*
    private void startThread() {
        Runnable executeTransfer = () -> {
            try {
                executeTransfer();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(executeTransfer);
        thread.start();
    }

    private void executeTransfer() throws InterruptedException {

        //TODO: call transfer creation

        String name = Thread.currentThread().getName();
        TimeUnit.SECONDS.sleep(10);
        log.info("Bar " + name);

        Transfer transfer = transferRepository.findOne(transferId);
        transfer.setStatus(TransferStatus.SUCCESSFUL);
        transferRepository.save(transfer);
    }
*/
    public String sendTransaction(Optional<String> account, Optional<Long> amount) {
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
        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        log.info("Send transaction response: " + response.getResult());

        /*
        byte[] bytes = DatatypeConverter.parseHexBinary(response.getResult().substring(2));
        String convertedResult = new String(bytes, StandardCharsets.UTF_8);
        log.info("Converted to string: " + convertedResult);

        long longResult = Long.parseLong(response.getResult().substring(2), 16);
        log.info("Converted to long: " + longResult);
        */

        return response.getResult();
    }

}
