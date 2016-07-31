package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Balance {
    @NonNull
    private Long amount;
    @NonNull
    private Currency currency;
}
