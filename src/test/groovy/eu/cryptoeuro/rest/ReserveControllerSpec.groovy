package eu.cryptoeuro.rest

import com.fasterxml.jackson.databind.ObjectMapper
import eu.cryptoeuro.TestUtils
import eu.cryptoeuro.rest.ReserveController
import eu.cryptoeuro.rest.model.Balance
import eu.cryptoeuro.rest.model.Currency
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class ReserveControllerSpec extends Specification {

    private ReserveController controller = new ReserveController()
    private static ObjectMapper mapper = new ObjectMapper()
    private MockMvc mockMvc

    def setup() {
        mockMvc = TestUtils.getMockMvc(controller)
    }

    def "get reserve"() {
        when:
        MockHttpServletResponse response = mockMvc.perform(get("/v1/reserve/")).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Balance balance = mapper.readValue(response.contentAsString, Balance.class);
        balance.amount.compareTo(new Long(0)).equals(1)
        balance.currency == Currency.EUR_CENT
    }
}
