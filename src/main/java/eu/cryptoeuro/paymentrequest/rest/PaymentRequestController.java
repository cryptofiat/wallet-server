package eu.cryptoeuro.paymentrequest.rest;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bitcoin.protocols.payments.Protos;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payment-request")
@Slf4j
public class PaymentRequestController {

    @ApiOperation(value = "Create a payment request.")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public Protos.PaymentRequest createPaymentRequest(){
        return Protos.PaymentRequest.newBuilder().build();
    }

}
