package eu.cryptoeuro.rest.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Nonce {

    @NonNull
    private Long nonce;

}
