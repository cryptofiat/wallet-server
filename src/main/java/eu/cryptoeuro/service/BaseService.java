package eu.cryptoeuro.service;

import org.springframework.web.client.RestTemplate;

public abstract class BaseService {
    protected static final String URL = "http://54.194.239.231:8545";
    protected RestTemplate restTemplate = new RestTemplate();
}
