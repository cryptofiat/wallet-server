package eu.cryptoeuro.rest.model;

import eu.cryptoeuro.rest.model.Transfer;
import java.util.List;
import java.io.Serializable;

public class TransferHistory implements Serializable {

    public List<Transfer> transferList;
    public String address;
    public long lastBlock;

    public TransferHistory() {
    };

}
