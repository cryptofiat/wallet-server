package eu.cryptoeuro.config;

import eu.cryptoeuro.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContractConfig {

    private Optional<String> ACCOUNT_CONTRACT_ADDRESS = Optional.empty();
    private Optional<String> DELEGATION_CONTRACT_ADDRESS = Optional.empty();

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
        ACCOUNT_CONTRACT_ADDRESS = Optional.of(ACCOUNT_CONTRACT_ADDRESS.orElse(contractService.getContract(1)));
        return ACCOUNT_CONTRACT_ADDRESS.orElseThrow(() -> new RuntimeException("Can't fetch account contract address"));
    }

    public String getDelegationContractAddress() {
        DELEGATION_CONTRACT_ADDRESS = Optional.of(DELEGATION_CONTRACT_ADDRESS.orElse(contractService.getContract(6)));
        return DELEGATION_CONTRACT_ADDRESS.orElseThrow(() -> new RuntimeException("Can't fetch delegation contract address"));
    }

}
