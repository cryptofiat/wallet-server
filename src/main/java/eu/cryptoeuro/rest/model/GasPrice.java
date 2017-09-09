package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class GasPrice {
    @NonNull
    public Long gasPriceWei;
}
