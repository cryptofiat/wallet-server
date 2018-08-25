package eu.cryptoeuro.service.rpc;

import lombok.Data;

import java.util.List;

@Data
public class JsonRpcBaseResponse {

    private JsonRpcErrorEntity error;

    public boolean hasError() {
        if (error != null) {
            return true;
        } else {
            return false;
        }
    }

}
