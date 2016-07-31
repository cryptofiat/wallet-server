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
public class BalanceService extends BaseService {

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
        params.put("to", CONTRACT);
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250

        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("balanceOf(address)").substring(0, 8) + accountArgument;

        log.info("Data: " + data);

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

}
