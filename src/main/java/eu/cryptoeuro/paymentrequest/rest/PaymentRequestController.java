package eu.cryptoeuro.paymentrequest.rest;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static org.bitcoin.protocols.payments.Protos.PaymentRequest;
import static org.bitcoin.protocols.payments.Protos.PaymentDetails;

@RestController
@RequestMapping("/v1/payment-request")
@Slf4j
public class PaymentRequestController {

    @ApiOperation(value = "Store a payment request.")
    @RequestMapping(method = RequestMethod.POST, value = "", consumes = "application/octet-stream")
    public String storePaymentRequest(RequestEntity<InputStream> entity){
        String result;
        try {
            PaymentRequest paymentRequest = PaymentRequest.parseFrom(entity.getBody());
            result = paymentRequest.getPkiType();
        } catch (IOException e) {
            return e.getMessage();
        }
        return "euro2:?r=http://wallet.euro2.ee:8080/v1/payment-request/abcdefg" + result;
    }

    @ApiOperation(value = "Get a Payment Request by ID.")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/bitcoin-paymentrequest")
    public PaymentRequest getPaymentRequest(@PathVariable("id") String id){
        PaymentDetails paymentDetails = PaymentDetails.newBuilder().setTime(Instant.now().toEpochMilli()).build();
        return PaymentRequest.newBuilder().setSerializedPaymentDetails(paymentDetails.toByteString()).build();
    }

}
