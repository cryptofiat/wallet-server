package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eu.cryptoeuro.config.ContractConfig;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.rest.model.Currency;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class BalanceService extends BaseService {

    ContractConfig contractConfig;

    @Autowired
    public BalanceService(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }

    public Balance getEtherBalance(String account) {
        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.getBalance, Arrays.asList(account, "latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

        JsonRpcStringResponse response = restTemplate.postForObject(contractConfig.getAccountContractAddress(), request, JsonRpcStringResponse.class);
        long responseToLong = Long.parseLong(response.getResult().substring(2).trim(), 16);
        log.info("Ether balance for " + account + ": " + responseToLong);

        return new Balance(responseToLong, Currency.ETH);
    }

    public Balance getBalance(String account) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_call

        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("balanceOf(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getAccountContractAddress());
        params.put("data", data);
        //params.put("gas", "0x76c0"); // 30400
        //params.put("gasPrice", "0x9184e72a000"); // 10000000000000
        //params.put("value", "0x9184e72a"); // 2441406250
        //params.put("nonce", "");

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        long responseToLong = Long.parseLong(response.getResult().substring(2).trim(), 16);
        log.info("EUR_CENT balance for " + account + ": " + responseToLong);

        return new Balance(responseToLong, Currency.EUR_CENT);
    }

}
