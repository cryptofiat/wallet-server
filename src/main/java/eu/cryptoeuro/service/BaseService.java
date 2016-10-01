package eu.cryptoeuro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public abstract class BaseService {

    @Value("${ethereum.node.url}")
    protected String URL;
    @Value("${sponsor.ethereum.address}")
    protected String SPONSOR;
    @Value("${contract.ethereum.address}")
    protected String CONTRACT;
    protected RestTemplate restTemplate = new RestTemplate();

}
