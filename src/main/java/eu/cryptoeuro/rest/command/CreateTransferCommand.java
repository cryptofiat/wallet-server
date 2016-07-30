package eu.cryptoeuro.rest.command;

import lombok.Data;

@Data
public class CreateTransferCommand {
    private String targetAccount;

}
