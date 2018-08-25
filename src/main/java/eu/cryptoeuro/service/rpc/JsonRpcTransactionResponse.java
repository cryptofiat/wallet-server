package eu.cryptoeuro.service.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
public class JsonRpcTransactionResponse extends JsonRpcBaseResponse {

    public JsonRpcTransactionEntity result;

    public boolean isMined() {
      return (result != null && result.getBlockNumber() > 0) ? true : false;
    }
    public String getBlockHash() {
	return (result != null) ? result.getBlockHash() : "";
    }

}
