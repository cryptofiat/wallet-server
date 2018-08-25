package eu.cryptoeuro.service;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCallMap;
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse;
import eu.cryptoeuro.rest.model.ContractInfo;

@Component
@Slf4j
public class ContractService extends BaseService {

    static final private int DATA             = 1;
    static final private int ACCOUNTS         = 2;
    static final private int APPROVING        = 3;
    static final private int RESERVE          = 4;
    static final private int ENFORCEMENT      = 5;
    static final private int ACCOUNT_RECOVERY = 6;
    static final private int DELEGATION       = 7;

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

    public List<String> getAllContracts() {

        boolean working = true;
        int i = 0;
        List<String> allContracts = new ArrayList<>();
        while(working) {
            Optional<String> contractAddress = getContractsHistory(new Long(i));
            if(!contractAddress.isPresent()) {
                working = false;
            }

            contractAddress.ifPresent(a -> {
                allContracts.add(a);
            });

            i++;
        }

        return allContracts;
    }

    private Optional<String> getContractsHistory(Long contractOrder) {
        String contractNumberArgument = HashUtils.padLongToUint(contractOrder);
        String signatureAndArgumentHash = "0x" + HashUtils.getContractSignatureHash("contracts(uint256)")  + contractNumberArgument;

        Map<String, String> params = new HashMap<>();
        params.put("to", CONTRACT);
        params.put("data", signatureAndArgumentHash);

        JsonRpcCallMap call = new JsonRpcCallMap(EthereumRpcMethod.call, Arrays.asList(params, "latest"));

        JsonRpcStringResponse response = getCallResponseForObject(call, JsonRpcStringResponse.class);

        if(isEmptyResponse(response)) {
            return Optional.empty();
        }

        log.info(response.getResult().toString());

        return Optional.of(HashUtils.padAddressTo40(response.getResult().toString()));
    }

    private boolean isEmptyResponse(JsonRpcStringResponse response) {
        if (response.getResult() == null) {
            return true;
        } else {
            return response.getResult().substring(2).trim().isEmpty();
        }
    }

    public ContractInfo contractInfo() {

	ContractInfo ci = new ContractInfo();
	ci.data = getContract(DATA);
	ci.accounts = getContract(ACCOUNTS);
	ci.accountApproving = getContract(APPROVING);
	ci.reserveBank = getContract(RESERVE);
	ci.enforcement = getContract(ENFORCEMENT);
	ci.accountRecovery = getContract(ACCOUNT_RECOVERY);
	ci.delegation = getContract(DELEGATION);
	//TODO: add the base contract without getting circular dependency with contractConfig
	//ci.base = contractConfig.getBaseContractAddress(); 
	return ci;
    }
}
