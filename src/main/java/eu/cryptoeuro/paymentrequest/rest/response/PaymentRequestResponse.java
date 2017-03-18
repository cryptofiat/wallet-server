package eu.cryptoeuro.paymentrequest.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequestResponse {
    boolean isRequesting;
    String requestorAddress;
    String adresseeAddress;
}
