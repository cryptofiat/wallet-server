package eu.cryptoeuro.rest.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
public class Transfer {

    @Id
    private String id;
    @Size(min = 64, max = 66) 
    private String targetAccount;
    @Size(min = 40, max = 42)
    private String sourceAccount;
    @Size(min = 40, max = 42)
    private TransferStatus status;
    @NotNull
    @Min(1)
    private Long amount;
    private Long fee;
    @NotNull
    private Long nonce;
    private String reference;
    @NotNull
    private Long sigV;
    @NotNull
    private String sigR;
    @NotNull
    private String sigS;
}
