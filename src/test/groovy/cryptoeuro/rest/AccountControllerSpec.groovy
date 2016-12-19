package eu.cryptoeuro.rest

import com.fasterxml.jackson.databind.ObjectMapper
import eu.cryptoeuro.TestUtils
import eu.cryptoeuro.rest.model.Balance
import eu.cryptoeuro.rest.model.Currency
import eu.cryptoeuro.rest.model.Nonce
import eu.cryptoeuro.service.AccountService
import eu.cryptoeuro.service.BalanceService
import eu.cryptoeuro.service.NonceService
import eu.cryptoeuro.service.TransferService
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import eu.cryptoeuro.fixture.AccountFixture
import eu.cryptoeuro.rest.model.Account

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class AccountControllerSpec extends Specification {

    private AccountController controller = new AccountController()
    private static ObjectMapper mapper = new ObjectMapper()
    private MockMvc mockMvc

    private sampleBalance = new Balance(1000, Currency.EUR_CENT)


    def setup() {
        mockMvc = TestUtils.getMockMvc(controller)
        controller.accountService = Mock(AccountService)
        controller.balanceService = Mock(BalanceService)
        controller.nonceService = Mock(NonceService)
        controller.transferService = Mock(TransferService)
    }

    def "get account by address"() {
        given:
        respondWithSampleAccount()
        when:
        MockHttpServletResponse response = mockMvc.perform(get("/v1/accounts/" + AccountFixture.sampleAccount().address)).andReturn().response
        then:
        response.status == HttpStatus.OK.value()
        Account account = mapper.readValue(response.contentAsString, Account.class);
        account.address == AccountFixture.sampleAccount().address
        account.approved == AccountFixture.sampleAccount().approved
        account.balance == AccountFixture.sampleAccount().balance
        account.closed == AccountFixture.sampleAccount().closed
        account.frozen == AccountFixture.sampleAccount().frozen
        account.nonce == AccountFixture.sampleAccount().nonce
    }

    def "get account transfers "() {
        
    }

    def respondWithSampleAccount() {
        1 * controller.accountService.isApproved(AccountFixture.sampleAccount().address) >> AccountFixture.sampleAccount().approved
        1 * controller.accountService.isClosed(AccountFixture.sampleAccount().address) >> AccountFixture.sampleAccount().closed
        1 * controller.accountService.isFrozen(AccountFixture.sampleAccount().address) >> AccountFixture.sampleAccount().frozen
        1 * controller.nonceService.getDelegatedNonceOf(AccountFixture.sampleAccount().address) >> new Nonce(AccountFixture.sampleAccount().nonce)
        1 * controller.balanceService.getBalance(AccountFixture.sampleAccount().address) >> new Balance(AccountFixture.sampleAccount().balance, Currency.EUR_CENT)
    }


}