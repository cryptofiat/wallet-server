package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.config.ContractConfig;
import eu.cryptoeuro.rest.model.Nonce;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class NonceService extends BaseService {

    private ContractConfig contractConfig;

    @Autowired
    public NonceService(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }

    public Nonce getDelegatedNonceOf(String account) {
        String accountArgument = "000000000000000000000000" + account.substring(2);
        String data = "0x" + HashUtils.keccak256("nonceOf(address)").substring(0, 8) + accountArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getDelegationContractAddress());
        params.put("data", data);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        String trimResponse = response.getResult().substring(2).trim();
        Long responseLong = new Long(0);
        if(!trimResponse.isEmpty()) {
            responseLong = Long.parseLong(trimResponse, 16);
        }
        log.info("delegatedNonceOf for " + account + ": " + responseLong);

        return new Nonce(responseLong);
    }

}
