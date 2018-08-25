package eu.cryptoeuro.service.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class JsonRpcTransactionLogResponse extends JsonRpcBaseResponse{

    private List<JsonRpcTransactionLogEntry> result;

    public List<JsonRpcTransactionLogEntry> getResult() {
        return result;
    }
}
