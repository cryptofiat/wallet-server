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
    @Size(min = 1, max = 256)
    private String targetAccount;
    @Size(min = 1, max = 256)
    private String sourceAccount;
    private TransferStatus status;
    @NotNull
    @Min(1)
    private Long amount;
    private Long fee;
}
