package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.config.ContractConfig;
import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.rest.model.Currency;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class SupplyService extends BaseService {

    private ContractConfig contractConfig;

    @Autowired
    public SupplyService(ContractConfig contractConfig) {
        this.contractConfig = contractConfig;
    }

    public Balance getTotalSupply() {
        // TODO: Make totalSupply public in etherium contract and test following code
        /*
        String data = "0x" + HashUtils.keccak256("totalSupply()").substring(0, 8);

        Map<String, String> params = new HashMap<>();
        params.put("to", contractConfig.getAccountContractAddress());
        params.put("data", data);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));
        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        log.info(response.getResult());
        long responseToLong = Long.parseLong(response.getResult().substring(2).trim(), 16);
        */
        long responseToLong = new Long(777);
        log.info("FAKE DATA! Total EUR_CENT supply in etherium network: " + responseToLong);

        return new Balance(responseToLong, Currency.EUR_CENT);

    }

}
