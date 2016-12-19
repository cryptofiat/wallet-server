package eu.cryptoeuro.rest

import com.fasterxml.jackson.databind.ObjectMapper
import eu.cryptoeuro.TestUtils
import eu.cryptoeuro.rest.model.Transfer
import eu.cryptoeuro.fixture.TransferFixture
import eu.cryptoeuro.service.TransferService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

class TransferControllerSpec extends Specification {

    private TransferController controller = new TransferController();
    private static ObjectMapper mapper = new ObjectMapper()
    private MockMvc mockMvc

    def setup() {
        mockMvc = TestUtils.getMockMvc(controller)
        controller.transferService = Mock(TransferService)
    }


    def "getTransfer: get transfer by id"() {
        given:
        1 * controller.transferService.get(TransferFixture.sampleTransfer().getId()) >> TransferFixture.sampleTransfer()
        when:
        MockHttpServletResponse response = mockMvc.perform(get("/v1/transfers/"+TransferFixture.sampleTransfer().getId())).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Transfer transfer = mapper.readValue(response.contentAsString, Transfer.class);
        transfer.getId() == TransferFixture.sampleTransfer().getId()
    }

    def "postWithTransferRequest: Creates a delegated transfer"() {
        given:
        1 * controller.transferService.delegatedTransfer(TransferFixture.sampleCreateTransferCommand()) >> TransferFixture.sampleTransfer()
        when:
        MockHttpServletResponse response = mockMvc
                .perform(post("/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(TransferFixture.sampleCreateTransferCommand()))).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Transfer transfer = mapper.readValue(response.contentAsString, Transfer.class);
        transfer.getId() == TransferFixture.sampleTransfer().getId()
    }

    def "postWithSepaTransferRequest: Create a bank transfer"() {
        given:
        1 * controller.transferService.delegatedBankTransfer(TransferFixture.sampleCreateBankTransferCommand()) >> TransferFixture.sampleTransfer()
        when:
        MockHttpServletResponse response = mockMvc
                .perform(post("/v1/transfers/bank")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(TransferFixture.sampleCreateBankTransferCommand()))).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Transfer transfer = mapper.readValue(response.contentAsString, Transfer.class);
        transfer.getId() == TransferFixture.sampleTransfer().getId()
    }

}
