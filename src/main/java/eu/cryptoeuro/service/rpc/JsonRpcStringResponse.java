package eu.cryptoeuro.service.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class JsonRpcStringResponse extends JsonRpcBaseResponse {

    private String result;

}
