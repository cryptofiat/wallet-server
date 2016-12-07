package eu.cryptoeuro.service

import org.springframework.beans.factory.annotation.Autowired

class ContractServiceSpec extends WireMockBaseSpec {

    @Autowired
    ContractService contractService

    def "getContract: Gets a single contract address, that is associated with the main contract"() {
        when:
        String contractAddress = contractService.getContract(1)
        then:
        contractAddress.equals("0x95f5c59e3f0f994e9fe66ea6ede76b22161551b5")
    }

    def "getContract: Gets a single contract address, with an order number which is out of scope"() {
        when:
        String contractAddress = contractService.getContract(999)
        then:
        contractAddress.equals("0x0000000000000000000000000000000000000000")
    }

    def "getAllContracts: Gets all contracts, which are associated with the main contract"() {
        when:
        List<String> allContractAddresses = contractService.getAllContracts()
        then:
        allContractAddresses.equals(Arrays.asList(
            "0xa10a263d4336e4466502b2889d27d04582a86663",
            "0x95f5c59e3f0f994e9fe66ea6ede76b22161551b5",
            "0x3e11e52b136d12e0344d4d415897bfb755991fd3",
            "0xa5f9b79fc7f067df25a795685493514a295a8a81",
            "0x780dd5524c4252a7896717b1f637ab9f297477aa",
            "0x098acd69e56596fd3ed83231f993421ba4388617",
            "0x7537743f428c980c107ce325ad7f58f00134b460",
            "0xaf71e622792f47119411ce019f4ca1b8d993496e"
        ))
    }


}
