package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;

@Component
@Slf4j
public class ContractService extends BaseService {

    public String getContract(long contractNumber) {
        String contractNumberArgument = HashUtils.padLongToUint(contractNumber);
        String signatureAndArgumentHash = "0x" + HashUtils.getContractSignatureHash("contractAddress(uint256)")  + contractNumberArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", CONTRACT);
        params.put("data", signatureAndArgumentHash);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        log.info(response.getResult().toString());

        return HashUtils.padAddressTo40(response.getResult().toString());
    }

}
