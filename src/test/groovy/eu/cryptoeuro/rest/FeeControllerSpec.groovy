package cryptoeuro.rest

import com.fasterxml.jackson.databind.ObjectMapper
import eu.cryptoeuro.FeeConstant
import eu.cryptoeuro.TestUtils
import eu.cryptoeuro.rest.FeeController
import eu.cryptoeuro.rest.model.Fee
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class FeeControllerSpec extends Specification {

    private FeeController controller = new FeeController()
    private static ObjectMapper mapper = new ObjectMapper()
    private MockMvc mockMvc

    def setup() {
        mockMvc = TestUtils.getMockMvc(controller)
    }

    def "get fees"() {
        when:
        MockHttpServletResponse response = mockMvc.perform(get("/v1/fees/")).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Fee fee = mapper.readValue(response.contentAsString, Fee.class);
        fee.amount == FeeConstant.FEE
        fee.amount_to_sepa == FeeConstant.FEE_TO_SEPA
    }
}
