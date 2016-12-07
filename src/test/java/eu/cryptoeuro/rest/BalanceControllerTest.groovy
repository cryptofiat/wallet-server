package eu.cryptoeuro.rest

import eu.cryptoeuro.TestUtils
import eu.cryptoeuro.rest.model.Balance
import eu.cryptoeuro.rest.model.Currency
import eu.cryptoeuro.service.BalanceService
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class BalanceControllerTest extends Specification {
/*
    private BalanceController controller = new BalanceController()
    private sampleBalance = new Balance(1000, Currency.EUR_CENT)
    private sampleAccountAddress = "0x65fa6548764C08C0DD77495B33ED302d0C212691";
    private MockMvc mockMvc


    def setup() {
        mockMvc = TestUtils.getMockMvc(controller)
        controller.balanceService = Mock(BalanceService)
    }

    @Ignore
    def "get balance for default account"() {
        given:
        controller.balanceService.getBalance(sampleAccountAddress) >> sampleBalance
        when:
        MockHttpServletResponse response = mockMvc.perform(get("/v1/balances/"+sampleAccountAddress)).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        response.contentAsString == '{"amount":1000,"currency":"EUR_CENT"}'
    }
*/
}
