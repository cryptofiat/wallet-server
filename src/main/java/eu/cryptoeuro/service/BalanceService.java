package eu.cryptoeuro.service;

import com.google.common.collect.Lists;
import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.service.rpc.EthereumRpcMethod;
import eu.cryptoeuro.service.rpc.JsonRpcCall;
import eu.cryptoeuro.service.rpc.JsonRpcResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class BalanceService {

	private RestTemplate restTemplate = new RestTemplate(); //new HttpComponentsClientHttpRequestFactory()

    public Balance getBalance(Optional<String> account) {
        String url = "http://54.194.239.231:8545";

        JsonRpcCall call = new JsonRpcCall(EthereumRpcMethod.getBalance,
                Arrays.asList("0x65fa6548764C08C0DD77495B33ED302d0C212691","latest"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> request = new HttpEntity<String>(call.toString(), headers);

    	JsonRpcResponse response = restTemplate.postForObject(url, request, JsonRpcResponse.class);

    	log.info(response.getResult());

        return new Balance(new BigDecimal(1000), "transferId");
    }

}
