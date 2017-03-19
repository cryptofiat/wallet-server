package eu.cryptoeuro.rest.command;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.AssertTrue;

import lombok.Data;

@Data
public class NotifyTransferCommand {

    @NotNull
    @Size(min = 1, max = 256)
    private String transactionHash;
    @NotNull
    @Min(1)
    private Long amount;
    private Long timestamp;
    private String referenceText;
    private String address;
    private String idCode;
}
