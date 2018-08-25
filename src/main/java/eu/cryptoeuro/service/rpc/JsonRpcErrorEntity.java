package eu.cryptoeuro.service.rpc;

import lombok.Data;
import java.util.Date;

@Data
public class JsonRpcErrorEntity {

    public String message;
    public String data;
    public Long code;

}
