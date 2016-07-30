package eu.cryptoeuro.service.rpc;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

import org.json.JSONObject;

@Data
public class JsonRpcCallMap {

    private String jsonrpc = "2.0";
    @NonNull
    private EthereumRpcMethod method;
    @NonNull
    private List<Object> params;
    private Long id = new Long(1);

    public String getMethod(){
        return method.toString();
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

}
