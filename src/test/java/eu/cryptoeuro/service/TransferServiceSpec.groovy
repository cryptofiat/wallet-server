package eu.cryptoeuro.service

import eu.cryptoeuro.FeeConstant
import eu.cryptoeuro.config.ContractConfig
import eu.cryptoeuro.rest.command.CreateTransferCommand
import eu.cryptoeuro.rest.model.Transfer
import eu.cryptoeuro.service.exception.FeeMismatchException
import eu.cryptoeuro.service.rpc.JsonRpcStringResponse
import eu.cryptoeuro.util.KeyUtil
import org.ethereum.crypto.ECKey
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class TransferServiceSpec extends Specification {

    AccountService accountService = Mock(AccountService)
    ContractConfig contractConfig = Mock(ContractConfig)
    KeyUtil keyUtil = Mock(KeyUtil)
    TransferService transferService = new TransferService(contractConfig, accountService, keyUtil)

    String sampleAddress = "0x65fa6548764C08C0DD77495B33ED302d0C212691"
    String sampleNumber =  "0x0000000000000000000000000000000000000001"

    def setup() {
        contractConfig.delegationContractAddress >> sampleAddress

        transferService.SPONSOR = sampleAddress
        transferService.URL = "https://sample.url"

        transferService.restTemplate = Mock(RestTemplate)
    }

    def "DelegatedTransfer: Create a delegated transfer"() {
        given:
        givenAccountsAreApproved()
        givenEthCallRespondsWithANumber(3)
        givenKeyIsSet()
        when:
        Transfer transfer = transferService.delegatedTransfer(sampleCreateTransferCommand())
        then:
        transfer != null
    }

    def "DelegatedTransfer: Wrong fee prevents transfer creation"() {
        given:
        givenAccountsAreApproved()
        when:
        Transfer transfer = transferService.delegatedTransfer(sampleCreateTransferCommandWithNotAcceptedFee())
        then:
        thrown FeeMismatchException

    }

    def "DelegatedTransfer: Not present key prevents transfer creation"() {
        given:
        givenAccountsAreApproved()
        when:
        Transfer transfer = transferService.delegatedTransfer(sampleCreateTransferCommand())
        then:
        thrown RuntimeException

    }

    def givenEthCallRespondsWithANumber(int n) {
        n * transferService.restTemplate.
                postForObject(transferService.URL, _, JsonRpcStringResponse.class) >> new JsonRpcStringResponse(result:sampleNumber)
    }

    def givenKeyIsSet() {
        def key = Mock(ECKey)
        key.getPrivKeyBytes() >> new byte[100]
//        key.priv = new BigInteger(100)
        1 * keyUtil.getWalletServerSponsorKey() >> key
    }

    def givenAccountsAreApproved() {
        2 * accountService.isApproved(_ as String) >> true
    }

    def "GetAll"() {

    }

    def "GetTransfersForAccount"() {

    }

    def "Get"() {

    }

    CreateTransferCommand sampleCreateTransferCommand() {
        return new CreateTransferCommand(
                sourceAccount: "123",
                targetAccount: "321",
                fee: FeeConstant.FEE,
                signature: "6865726F6E6779616E672E636F6D",
                amount: new Long(123),
                nonce: new Long(1)
        )
    }

    CreateTransferCommand sampleCreateTransferCommandWithNotAcceptedFee() {
        return new CreateTransferCommand(
                sourceAccount: "123",
                targetAccount: "321",
                fee: new Long(FeeConstant.FEE + 1)
        )
    }

}
