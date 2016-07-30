package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class Fee {
    @NonNull
    private BigDecimal amount;
}
