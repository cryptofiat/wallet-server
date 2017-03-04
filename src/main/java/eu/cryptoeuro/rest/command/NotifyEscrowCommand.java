package eu.cryptoeuro.rest.command;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.AssertTrue;

import lombok.Data;

@Data
public class NotifyEscrowCommand {

    @NotNull
    @Size(min = 1, max = 256)
    public String email;
    public String senderFirstName;
    public String senderLastName;
    public String recipientFirstName;
    public String recipientLastName;
    @NotNull
    @Min(1)
    public Long amount;
    @NotNull
    public String escrowAddress;
    @NotNull
    public String transactionHash;

    public String amountString() {
	float frac = (float) amount / 100;
	return String.format("%.2f", frac);
    }
}
