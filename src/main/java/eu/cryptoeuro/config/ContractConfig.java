package eu.cryptoeuro.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.service.ContractService;

@Component
public class ContractConfig {

    private Optional<String> ACCOUNT_CONTRACT_ADDRESS = Optional.empty();
    private Optional<String> DELEGATION_CONTRACT_ADDRESS = Optional.empty();
    private Optional<List<String>> CONTRACT_LIST = Optional.empty();

    private ContractService contractService;

    @Value("${contract.ethereum.address}")
    private String BASE_CONTRACT_ADDRESS;

    @Autowired
    public ContractConfig(ContractService contractService) {
        this.contractService = contractService;
    }

    public String getBaseContractAddress() {
        return BASE_CONTRACT_ADDRESS;
    }

    public String getAccountContractAddress() {
        ACCOUNT_CONTRACT_ADDRESS = getContractAddress(ACCOUNT_CONTRACT_ADDRESS, 2);
        return ACCOUNT_CONTRACT_ADDRESS.orElseThrow(() -> new RuntimeException("Can't fetch account contract address"));
    }

    public String getDelegationContractAddress() {
        DELEGATION_CONTRACT_ADDRESS = getContractAddress(DELEGATION_CONTRACT_ADDRESS, 7);
        return DELEGATION_CONTRACT_ADDRESS.orElseThrow(() -> new RuntimeException("Can't fetch delegation contract address"));
    }

    public List<String> getAllContracts() {
        if(!CONTRACT_LIST.isPresent()) {
            CONTRACT_LIST = Optional.of(contractService.getAllContracts());
        }

        return CONTRACT_LIST.orElseThrow(() -> new RuntimeException("Can't fetch all contracts"));
    }

    private Optional<String> getContractAddress(Optional<String> current, int n) {
        if(!current.isPresent()) {
            current = Optional.of(contractService.getContract(n));
        }

        return current;
    }

}
