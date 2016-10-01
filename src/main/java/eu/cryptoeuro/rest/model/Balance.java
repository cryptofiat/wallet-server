package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Balance {

    @NonNull
    private Long amount;
    @NonNull
    private Currency currency;

}
