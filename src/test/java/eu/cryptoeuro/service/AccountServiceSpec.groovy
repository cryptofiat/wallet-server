package eu.cryptoeuro.service

import org.springframework.beans.factory.annotation.Autowired

class AccountServiceSpec extends WireMockBaseSpec {

    @Autowired
    AccountService accountService

    String sampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212691";
    String nonExistingSampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212690";
    String invalidSampleAccount = "0x123";

    def "isApproved: Gets if account is approved"() {
        when:
        Boolean isApproved = accountService.isApproved(sampleAccount)
        then:
        isApproved.equals(true)
    }

    def "isApproved: Gets false if account does not exist"() {
        when:
        Boolean isApproved = accountService.isApproved(nonExistingSampleAccount)
        then:
        isApproved.equals(false)
    }

    def "isApproved: Throws exception on getting is account approved on invalid"() {
        when:
        Boolean isApproved = accountService.isApproved(invalidSampleAccount)
        then:
        thrown NullPointerException
    }

    def "isClosed: Gets if account is closed"() {
        when:
        Boolean isClosed = accountService.isClosed(sampleAccount)
        then:
        isClosed.equals(false)
    }

    def "isFrozen: Gets if account is frozen"() {
        when:
        Boolean isFrozen = accountService.isFrozen(sampleAccount)
        then:
        isFrozen.equals(false)
    }

}
