package eu.cryptoeuro.rest.model;

import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@Builder
//@AllArgsConstructor
public class Account {

    @NotNull
    private String address;
    @NotNull
    private boolean approved;
    @NotNull
    private boolean closed;
    @NotNull
    private boolean frozen;
    @NotNull
    private Long nonce;
    @NotNull
    private Long balance;


}
