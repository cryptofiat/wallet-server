package eu.cryptoeuro.rest.command;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

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
