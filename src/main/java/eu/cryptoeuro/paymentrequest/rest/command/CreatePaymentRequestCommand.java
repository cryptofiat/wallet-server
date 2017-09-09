package eu.cryptoeuro.paymentrequest.rest.command;

import eu.cryptoeuro.FeeConstant;
import eu.cryptoeuro.euro2paymenturi.Euro2PaymentURI;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;

@Data
public class CreatePaymentRequestCommand {
    @Getter
    String euro2PaymentUri;

    @AssertTrue(message="Unable to parse euro2PaymentUri")
    private boolean isValidEuro2PaymentUri() {
        if (Euro2PaymentURI.parse(this.euro2PaymentUri) != null) {
            return true;
        } else {
            return true;
        }
    }

    public Euro2PaymentURI toEuro2PaymentURI() {
        return Euro2PaymentURI.parse(this.euro2PaymentUri);
    }
}
