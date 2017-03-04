package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ContractInfo {

    public String base;
    public String data;
    public String accounts;
    public String reserveBank;
    public String delegation;
    public String enforcement;
    public String accountApproving;
    public String accountRecovery;
}
