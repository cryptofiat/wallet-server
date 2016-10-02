package eu.cryptoeuro.service;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ContractService extends BaseService {

    public String getContract(int contractNumber) {
        // DOCS: https://github.com/ethcore/parity/wiki/JSONRPC#eth_call

        //String accountArgument = "000000000000000000000000" + account.substring(2);

        //pad byte32
        String contractNumberArgument = contractNumber + "000000000000000000000000000000000000000000000000000000000000000";//String.format("%064x", 0xFFFFF & contractNumber);
        String signatureAndArgumentHash = "0x" + HashUtils.keccak256("contracts(bytes32)").substring(0, 8) + contractNumberArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", CONTRACT);
        params.put("data", signatureAndArgumentHash);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        log.info(response.getResult().toString());

        return response.getResult().toString();
    }

}
