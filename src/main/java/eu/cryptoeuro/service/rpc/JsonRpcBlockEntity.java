package eu.cryptoeuro.service.rpc;

import lombok.Data;
import java.util.Date;
import eu.cryptoeuro.service.HashUtils;

@Data
public class JsonRpcBlockEntity {

    private String timestamp;
    String parentHash;
    String number;


	public Date getTimestamp() {
           return new Date(Long.parseLong(HashUtils.without0x(timestamp), 16) * 1000);
	}
}
