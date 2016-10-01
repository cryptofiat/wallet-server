package eu.cryptoeuro.rest.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

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
    private String signature;

}
