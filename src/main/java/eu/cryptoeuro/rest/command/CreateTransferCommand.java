package eu.cryptoeuro.rest.command;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateTransferCommand {
    @NotNull
    @Min(1)
    private String targetAccount;
}
