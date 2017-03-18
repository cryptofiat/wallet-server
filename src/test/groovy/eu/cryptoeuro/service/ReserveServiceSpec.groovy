package eu.cryptoeuro.service

import eu.cryptoeuro.rest.model.Balance
import eu.cryptoeuro.rest.model.Currency
import org.springframework.beans.factory.annotation.Autowired

class ReserveServiceSpec extends WireMockBaseSpec {

    @Autowired
    ReserveService reserveService

    def "getTotalReserve: get total reserve"() {
        when:
        Balance balance = reserveService.getTotalReserve()
        then:
        balance.amount.compareTo(new Long(0)).equals(1)
        balance.currency.equals(Currency.EUR_CENT)
    }

}
