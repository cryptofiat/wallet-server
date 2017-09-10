package eu.cryptoeuro.rest.command;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.AssertTrue;

import eu.cryptoeuro.FeeConstant;
import eu.cryptoeuro.service.exception.FeeMismatchException;
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
    //TODO: should be FeeConstant.FEE
    @Min(1)
    private Long fee;
    @NotNull
    @Min(1)
    private Long nonce;
    @NotNull
    private String signature;
    private Long paymentRequestId;

    @AssertTrue(message="Fee does not match expected")
    private boolean isValidFee() {
        if (FeeConstant.FEE.compareTo(this.fee) != 0) {
            //throw new FeeMismatchException(this.fee, FeeConstant.FEE);
	    return false;
        } else {
	    return true;
	}
    }
}
