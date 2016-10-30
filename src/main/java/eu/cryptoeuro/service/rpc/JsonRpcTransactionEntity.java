package eu.cryptoeuro.service.rpc;

import lombok.Data;
import java.util.Date;
import eu.cryptoeuro.service.HashUtils;

@Data
public class JsonRpcTransactionEntity {

    private String blockNumber;
    public String blockHash;
    public String input;

	public long getBlockNumber() {
           if (blockNumber == null) return 0;
           return Long.parseLong(HashUtils.without0x(blockNumber), 16);
	}
}
