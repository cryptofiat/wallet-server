package eu.cryptoeuro.service;

import eu.cryptoeuro.rest.model.Balance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@Slf4j
public class BalanceService {

    public Balance getBalance(Optional<String> account) {
        return new Balance(new BigDecimal(1000), "transferId");
    }

}
