package eu.cryptoeuro.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Balance {

    @NonNull
    private Long amount;
    @NonNull
    private Currency currency;

}
