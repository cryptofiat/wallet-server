package eu.cryptoeuro.service;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AccountService extends BaseService {

    public Boolean isApproved(String account) {
        log.info("Checking if account is approved for address: " + account);
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_call
        Map<String, String> params = new HashMap<>();
        params.put("to", CONTRACT);
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250

        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("approvedAccount(address)").substring(0, 8) + accountArgument;

        log.info("Data: " + data);

        params.put("data", data);
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);
        JsonRpcResponse response = restTemplate.postForObject(URL, request, JsonRpcResponse.class);

        log.info("Approved account response: " + response.getResult());

        String resp = response.getResult().substring(2);
        if(resp.length() == 0) {
            log.info("No response: " + response.getResult());
            return false;
        }

        boolean result = Integer.parseInt(resp) == 1;
        log.info("Converted to bool: " + result);

        return result;
    }

}
