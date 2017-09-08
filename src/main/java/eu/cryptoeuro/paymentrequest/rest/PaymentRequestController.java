package eu.cryptoeuro.paymentrequest.rest;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import eu.cryptoeuro.rest.model.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.Script;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.vm.program.Program;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import static org.bitcoin.protocols.payments.Protos.PaymentRequest;
import static org.bitcoin.protocols.payments.Protos.PaymentDetails;

@RestController
@RequestMapping("/v1/payment-request")
@Slf4j
public class PaymentRequestController {


    //TODO Current MVP implementation uses hex serialisation, upgrade to true protobuf (needs some hacking to make it work in spring)
    @ApiOperation(value = "Store a payment request.")
//    @RequestMapping(method = RequestMethod.POST, value = "", consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//    public String storePaymentRequest(@RequestBody InputStream requestInputStream){
    @RequestMapping(method = RequestMethod.POST, value = "" , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String storePaymentRequest(/*@RequestBody String requestHex*/ @RequestParam("requestHex") String requestHex){

        PaymentRequest paymentRequest = null;
        try {
            paymentRequest = PaymentRequest.parseFrom(Hex.decode(requestHex));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        PaymentDetails paymentDetails = null;

        try {
            paymentDetails = PaymentDetails.parseFrom(paymentRequest.getSerializedPaymentDetails());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        Script script = new Script(paymentDetails.getOutputs(0).getScript().toByteArray());


        Address toAddress = script.getToAddress(NetworkParameters.prodNet());
        String publicKeyHashOut = Hex.toHexString(script.getPubKeyHash());

        eu.cryptoeuro.rest.model.PaymentRequest.builder().adresseeAddress(toAddress.toString());
        log.info("test");

        return "euro2:?r=http://wallet.euro2.ee:8080/v1/payment-request/abcdefg";
    }

    @ApiOperation(value = "Get a Payment Request by ID.")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/x-protobuf")
    public PaymentRequest getPaymentRequest(@PathVariable("id") String id){
        PaymentDetails paymentDetails = PaymentDetails.newBuilder().setTime(Instant.now().toEpochMilli()).build();
        return PaymentRequest.newBuilder().setSerializedPaymentDetails(paymentDetails.toByteString()).build();
    }

}
