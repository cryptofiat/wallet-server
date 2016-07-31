package eu.cryptoeuro.rest.model;

import eu.cryptoeuro.FeeConstant;
import lombok.Data;
import lombok.NonNull;

@Data
public class Fee {
    @NonNull
    public Long amount = FeeConstant.FEE;
}
