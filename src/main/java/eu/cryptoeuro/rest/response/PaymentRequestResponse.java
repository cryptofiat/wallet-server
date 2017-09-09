package eu.cryptoeuro.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequestResponse {
    private Long id;
    String euro2PaymentUri;

    boolean isRequesting;

    String requestorAddress;
    String receiverAddress;
    String payerAddress;
}
