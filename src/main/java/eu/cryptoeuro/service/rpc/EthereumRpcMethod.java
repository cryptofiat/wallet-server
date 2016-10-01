package eu.cryptoeuro.service.rpc;

import java.io.Serializable;

public enum EthereumRpcMethod implements Serializable {

    getBalance("eth_getBalance"),
    getTransactionCount("eth_getTransactionCount"),
    sendTransaction("eth_sendTransaction"),
    sendRawTransaction("eth_sendRawTransaction"),
    call("eth_call"),
    logs("eth_getLogs");

    private final String method;

    EthereumRpcMethod(String method){
        this.method = method;
    }

    @Override
    public String toString() {
        return this.method;
    }

}
