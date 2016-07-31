package eu.cryptoeuro.service;

import org.springframework.web.client.RestTemplate;

public abstract class BaseService {
    protected static final String URL = "http://54.194.239.231:8545";
//    protected static final String URL = "http://localhost:8545";
    protected RestTemplate restTemplate = new RestTemplate();
    public final String SPONSOR = "0x65fa6548764C08C0DD77495B33ED302d0C212691";
    public final String CONTRACT = "0x640Da14959D6A6244f35471080BEBd960F15FDAe"; //0.31

}
