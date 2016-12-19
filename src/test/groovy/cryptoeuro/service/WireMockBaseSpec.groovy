package eu.cryptoeuro.service

import com.github.tomakehurst.wiremock.junit.WireMockClassRule
import org.junit.ClassRule
import org.junit.Rule;
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWireMock()
@ActiveProfiles("test")
public class WireMockBaseSpec extends Specification {

    @ClassRule
    public static WireMockClassRule ethereumWireMockRule = new WireMockClassRule(28545)
    @Rule
    public WireMockClassRule ethereumInstanceRule = ethereumWireMockRule


}
