package eu.cryptoeuro.rest.command;

import eu.cryptoeuro.rest.model.Fee;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
public class CreateTransferCommand {
    @NotNull
    @Size(min = 1, max = 256)
    private String sourceAccount;
    @NotNull
    @Size(min = 1, max = 256)
    private String targetAccount;
    @NotNull
    @Min(1)
    private Long amount;
    @Min(1) //TODO: externalize.. FeeConstant.FEE
    private Long fee;
    @NotNull
    @Min(1)
    private Long nonce;
    private String reference;
    @NotNull
    private Long sigV;
    @NotNull
    private String sigR;
    @NotNull
    private String sigS;
}
