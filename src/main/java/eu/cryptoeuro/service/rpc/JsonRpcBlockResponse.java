package eu.cryptoeuro.service.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
public class JsonRpcBlockResponse extends JsonRpcBaseResponse {

    public JsonRpcBlockEntity result;

    public Date getTimestamp() {
      return result.getTimestamp();
    }
}
