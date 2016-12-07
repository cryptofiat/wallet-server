package eu.cryptoeuro.service

import eu.cryptoeuro.rest.command.CreateBankTransferCommand
import eu.cryptoeuro.rest.command.CreateTransferCommand
import eu.cryptoeuro.rest.model.Transfer
import eu.cryptoeuro.rest.model.TransferStatus
import org.springframework.beans.factory.annotation.Autowired

class TransferServiceSpec extends WireMockBaseSpec {

    @Autowired
    TransferService transferService

    String sampleTransferId = "0x8f9c84ef5149591dbd36b8cdb1374963e8b9499b0a3a8f0c796398d7271377a2"
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
        Transfer transfer = transferService.delegatedTransfer(sampleCreateTransferCommand())
        then:
        transfer.id.equals(sampleTransferId)
        transfer.targetAccount.equals(sampleCreateTransferCommand().getTargetAccount())
        transfer.sourceAccount.equals(sampleCreateTransferCommand().getSourceAccount())
        transfer.status.equals(TransferStatus.PENDING)
        transfer.amount.equals(sampleCreateTransferCommand().getAmount())
        transfer.fee.equals(sampleCreateTransferCommand().getFee())
        transfer.nonce.equals(sampleCreateTransferCommand().getNonce())
        transfer.signature.equals(sampleCreateTransferCommand().getSignature())
    }

    def "delegatedBankTransfer: Performs a delegated bank transfer, which is signed by originator and delegated and signed by gateway"() {
        when:
        Transfer transfer = transferService.delegatedBankTransfer(sampleCreateBankTransferCommand())
        then:
        transfer.id.equals(sampleTransferId)
        transfer.targetAccount.equals(TransferService.bankProxyAddress)
        transfer.sourceAccount.equals(sampleCreateBankTransferCommand().getSourceAccount())
        transfer.status.equals(TransferStatus.PENDING)
        transfer.amount.equals(sampleCreateBankTransferCommand().getAmount())
        transfer.fee.equals(sampleCreateBankTransferCommand().getFee())
        transfer.nonce.equals(sampleCreateBankTransferCommand().getNonce())
        transfer.signature.equals(sampleCreateBankTransferCommand().getSignature())
    }

    CreateTransferCommand sampleCreateTransferCommand() {
        return [
                sourceAccount:"0x65fa6548764c08c0dd77495b33ed302d0c212691",
                targetAccount:"0x833898875a12a3d61ef18dc3d2b475c7ca3a4a72",
                amount:1,
                fee:1,
                nonce:2,
                signature:"cc193b8fe8c8b986f676fdb142cc9cea5c8369ef494b5207abdaf0de4a27efd07cfc8c5094b6418384169ab467fc9c5e880730a502c0b6b87c35eb30baa6a8431b"
            ]
    }

    CreateBankTransferCommand sampleCreateBankTransferCommand() {
        return [
                sourceAccount:"0x65fa6548764c08c0dd77495b33ed302d0c212691",
                targetBankAccountIBAN:"IBAN_NOT_USED_IN_ETH",
                amount:1,
                fee:1,
                nonce:2,
                signature:"cc193b8fe8c8b986f676fdb142cc9cea5c8369ef494b5207abdaf0de4a27efd07cfc8c5094b6418384169ab467fc9c5e880730a502c0b6b87c35eb30baa6a8431b"
        ]
    }
}
