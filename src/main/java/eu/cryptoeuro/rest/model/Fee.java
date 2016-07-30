package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Fee {
    @NonNull
    public final static Long amount = new Long(1);
}
