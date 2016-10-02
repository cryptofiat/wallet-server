package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.config.ContractConfig;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class AccountService extends BaseService {

    private ContractConfig contractConfig;

    @Autowired
    public AccountService(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }

    public Boolean isApproved(String account) {
        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("isApproved(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getAccountContractAddress());
        params.put("data", data);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        String resp = response.getResult().substring(2);
        log.info("Checking if account is approved for address: " + account + " -> " + resp);

        return resp.length() == 0 ? false : (Integer.parseInt(resp) == 1);
    }

    public Boolean isClosed(String account) {
        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("isClosed(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getAccountContractAddress());
        params.put("data", data);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        String resp = response.getResult().substring(2);
        log.info("Checking if account is approved for address: " + account + " -> " + resp);

        return resp.length() == 0 ? false : (Integer.parseInt(resp) == 1);
    }

    public Boolean isFrozen(String account) {
        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("isFrozen(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getAccountContractAddress());
        params.put("data", data);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        String resp = response.getResult().substring(2);
        log.info("Checking if account is approved for address: " + account + " -> " + resp);

        return resp.length() == 0 ? false : (Integer.parseInt(resp) == 1);
    }

}
