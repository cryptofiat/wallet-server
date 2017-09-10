package eu.cryptoeuro.service

import eu.cryptoeuro.rest.model.Nonce
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

class NonceServiceSpec extends WireMockBaseSpec {

    @Autowired
    NonceService nonceService

    String sampleContractAddress = "0x65fa6548764c08c0dd77495b33ed302d0c212691"
    String nonExistingSampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212690";
    String invalidSampleAccount = "0x123";

    def "getDelegatedNonceOf: Get a nonce that can be used for delegated transfer"() {
        when:
        Nonce nonce = nonceService.getDelegatedNonceOf(sampleContractAddress)
        then:
        nonce.nonce.equals(1L)
    }

    def "getDelegatedNonceOf: Get a nonce throws exception on invalid address"() {
        when:
        Nonce nonce = nonceService.getDelegatedNonceOf(invalidSampleAccount)
        then:
        thrown NullPointerException
    }

    def "getDelegatedNonceOf: Get a nonce on not existing account address"() {
        when:
        Nonce nonce = nonceService.getDelegatedNonceOf(nonExistingSampleAccount)
        then:
        nonce.nonce.equals(0L)
    }

}
