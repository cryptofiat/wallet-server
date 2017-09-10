package eu.cryptoeuro.service;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.rest.model.Currency;
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore;

public class BalanceServiceSpec extends WireMockBaseSpec {
    @Autowired
    BalanceService balanceService

    String sampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212691";
    String nonExistingSampleAccount = "0x65fa6548764c08c0dd77495b33ed302d0c212690";
    String invalidSampleAccount = "0x123";

    def "getEtherBalance: Gets Ether balance based on account address"(){
        when:
        Balance balance = balanceService.getEtherBalance(sampleAccount)
        then:
        balance.amount.equals(3072340492760131000)
        balance.currency.equals(Currency.ETH)
    }

    def "getBalance: Gets EUR balance based on account address"(){
        when:
        Balance balance = balanceService.getBalance(sampleAccount)
        then:
        balance.amount.equals(10L)
        balance.currency.equals(Currency.EUR_CENT)
    }

    def "getBalance: Throws exception when getting EUR balance based on invalid account address"(){
        when:
        Balance balance = balanceService.getBalance(invalidSampleAccount)
        then:
        thrown NullPointerException
    }

    def "getBalance: Gets EUR balance based on non exsisting account address"(){
        when:
        Balance balance = balanceService.getBalance(nonExistingSampleAccount)
        then:
        balance.amount.equals(0L)
        balance.currency.equals(Currency.EUR_CENT)
    }

}
