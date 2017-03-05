package eu.cryptoeuro.service.rpc;

import lombok.Data;
import java.util.Date;

@Data
public class JsonRpcTransactionResponse {

    public JsonRpcTransactionEntity result;

    public boolean isMined() {
      return (result != null && result.getBlockNumber() > 0) ? true : false;
    }
    public String getBlockHash() {
	return result.getBlockHash();
    }

}
