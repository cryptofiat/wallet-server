package eu.cryptoeuro.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.rest.model.Currency;

@Component
@Slf4j
public class ReserveService extends BaseService {

    public ReserveService() { }

    public Balance getTotalReserve() {
        // TODO: Get data from gataway API
        long responseToLong = new Long(666);
        log.info("FAKE DATA! Total EUR_CENT reserve in bank: " + responseToLong);

        return new Balance(responseToLong, Currency.EUR_CENT);

    }

}
