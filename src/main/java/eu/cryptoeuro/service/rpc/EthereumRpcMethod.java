package eu.cryptoeuro.service.rpc;

import java.io.Serializable;

public enum EthereumRpcMethod implements Serializable {
    getBalance("eth_getBalance");

    private final String method;
    EthereumRpcMethod(String method){
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }

    public String toString() {
        return this.method;
    }
}
