package eu.cryptoeuro.service

import eu.cryptoeuro.rest.model.Transfer
import eu.cryptoeuro.rest.model.TransferStatus
import eu.cryptoeuro.fixture.TransferFixture
import org.springframework.beans.factory.annotation.Autowired

class TransferServiceSpec extends WireMockBaseSpec {

    @Autowired
    TransferService transferService

    String sampleTransferId = "0x8f9c84ef5149591dbd36b8cdb1374963e8b9499b0a3a8f0c796398d7271377a2"
    String sampleBankTransferId = "0xbb85d120229ed89b347a3a12c35d0c034785f3a07256e385f594deba9f347951"
    String sampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212691";

    def "get: get a transfer by id"() {
        when:
        Transfer transfer = transferService.get(sampleTransferId)
        then:
        transfer.getId().equals(sampleTransferId)
        transfer.getStatus().equals(TransferStatus.PENDING)
    }

    //TODO complete the functionality and redo test
    def "getTransfersForAccount: get a transfer by account"() {
        when:
        List<Transfer> transfers = transferService.getTransfersForAccount(sampleAccount)
        then:
        transfers.size().equals(2)
    }

    def "delegatedTransfer: Performs a delegated transfer, which is signed by originator and delegated and signed by gateway"() {
        when:
        Transfer transfer = transferService.delegatedTransfer(TransferFixture.sampleCreateTransferCommand())
        then:
        transfer.id.equals(sampleTransferId)
        transfer.targetAccount.equals(TransferFixture.sampleCreateTransferCommand().getTargetAccount())
        transfer.sourceAccount.equals(TransferFixture.sampleCreateTransferCommand().getSourceAccount())
        transfer.status.equals(TransferStatus.PENDING)
        transfer.amount.equals(TransferFixture.sampleCreateTransferCommand().getAmount())
        transfer.fee.equals(TransferFixture.sampleCreateTransferCommand().getFee())
        transfer.nonce.equals(TransferFixture.sampleCreateTransferCommand().getNonce())
        transfer.signature.equals(TransferFixture.sampleCreateTransferCommand().getSignature())
    }

    def "delegatedBankTransfer: Performs a delegated bank transfer, which is signed by originator and delegated and signed by gateway"() {
        when:
        Transfer transfer = transferService.delegatedBankTransfer(TransferFixture.sampleCreateBankTransferCommand())
        then:
        transfer.id.equals(sampleBankTransferId)
        transfer.targetAccount.equals(TransferService.bankProxyAddress)
        transfer.sourceAccount.equals(TransferFixture.sampleCreateBankTransferCommand().getSourceAccount())
        transfer.status.equals(TransferStatus.PENDING)
        transfer.amount.equals(TransferFixture.sampleCreateBankTransferCommand().getAmount())
        transfer.fee.equals(TransferFixture.sampleCreateBankTransferCommand().getFee())
        transfer.nonce.equals(TransferFixture.sampleCreateBankTransferCommand().getNonce())
        transfer.signature.equals(TransferFixture.sampleCreateBankTransferCommand().getSignature())
    }

}
