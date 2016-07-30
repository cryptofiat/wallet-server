package eu.cryptoeuro.service.rpc;

import java.io.Serializable;

public enum EthereumRpcMethod implements Serializable {

    getBalance("eth_getBalance"),
    sendTransaction("eth_sendTransaction"),
    call("eth_call");

    private final String method;

    EthereumRpcMethod(String method){
        this.method = method;
    }

    @Override
    public String toString() {
        return this.method;
    }

}
