package eu.cryptoeuro.service;

import eu.cryptoeuro.rest.model.Balance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Slf4j
public class BalanceService {

    public Balance getBalance(Optional<String> account) {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8545",
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"0x65fa6548764C08C0DD77495B33ED302d0C212691\", \"latest\"],\"id\":1}",
                String.class);


        return new Balance(new BigDecimal(1000), "transferId");
    }

}
