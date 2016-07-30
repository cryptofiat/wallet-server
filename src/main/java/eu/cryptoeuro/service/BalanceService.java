package eu.cryptoeuro.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcResponse;

@Component
@Slf4j
public class BalanceService {

    private static final String URL = "http://54.194.239.231:8545";

    private RestTemplate restTemplate = new RestTemplate(); //new HttpComponentsClientHttpRequestFactory()

    public Balance getEtherBalance(Optional<String> account) {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.getBalance, Arrays.asList(account.orElse("0x65fa6548764C08C0DD77495B33ED302d0C212691"), "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);
        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        log.info("Response balance: " + response.getResult());
        return new Balance(new BigDecimal(
                Long.parseLong(response.getResult().substring(2).trim(), 16)
                ), "transferId");
    }

    public Long getBalance(String account) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_call
        Map<String, String> params = new HashMap<>();
        //params.put("from", "0x4FfAaD6B04794a5911E2d4a4f7F5CcCEd0420291"); // erko main account
        params.put("to", "0xAF8ce136A244dB6f13a97e157AC39169F4E9E445"); // viimane 0.21 contract deploy
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250

        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("balanceOf(address)").substring(0, 8) + accountArgument;

        params.put("data", data);
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);
        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        log.info("Send transaction response: " + response.getResult());

        byte[] bytes = DatatypeConverter.parseHexBinary(response.getResult().substring(2));
        String convertedResult = new String(bytes, StandardCharsets.UTF_8);
        log.info("Converted to string: " + convertedResult);

        long longResult = Long.parseLong(response.getResult().substring(2), 16);
        log.info("Converted to long: " + longResult);

        return longResult;
    }

    public String sendTransaction(Optional<String> account, Optional<Long> amount) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_sendtransaction
        Map<String, String> params = new HashMap<>();
        params.put("from", "0x4FfAaD6B04794a5911E2d4a4f7F5CcCEd0420291"); // erko main account
        params.put("to", "0xAF8ce136A244dB6f13a97e157AC39169F4E9E445"); // viimane 0.21 contract deploy
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250

        String targetArgument = "000000000000000000000000" + account.orElse("0x52C312631d5593D9164A257abcD5c58d14B96600").substring(2);
        // TODO use amount variable
        String amountArgument = "0000000000000000000000000000000000000000000000000000000000000064"; // 100 raha
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
