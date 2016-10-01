package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcResponse;

@Component
@Slf4j
public class AccountService extends BaseService {

    public Boolean isApproved(String account) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_call

        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("approved(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", CONTRACT);
        params.put("data", data);
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        String resp = response.getResult().substring(2);
        if (resp.length() == 0) {
            log.info("Checking if account is approved for address: " + account + " -> no response: " + response.getResult());
            return false;
        }

        boolean result = Integer.parseInt(resp) == 1;
        log.info("Checking if account is approved for address: " + account + " -> " + result);

        return result;
    }

}
