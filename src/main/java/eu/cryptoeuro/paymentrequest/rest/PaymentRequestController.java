package eu.cryptoeuro.paymentrequest.rest;

import eu.cryptoeuro.dao.PaymentRequestRepository;
import eu.cryptoeuro.euro2paymenturi.Euro2PaymentURI;
import eu.cryptoeuro.paymentrequest.rest.command.CreatePaymentRequestCommand;
import eu.cryptoeuro.rest.exception.ValidationException;
import eu.cryptoeuro.rest.model.PaymentRequest;
import eu.cryptoeuro.service.EthSignatureService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.ethereum.crypto.ECKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/payment-requests")
@Slf4j
public class PaymentRequestController {

    @Autowired
    PaymentRequestRepository paymentRequestRepository;

    @Autowired
    EthSignatureService ethSignatureService;

    @ApiOperation(value = "Submit payment request")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<String> submitPaymentRequest(
        @Valid @RequestBody @ApiParam CreatePaymentRequestCommand createPaymentRequestCommand,
        @ApiIgnore Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        Euro2PaymentURI euro2PaymentURI = createPaymentRequestCommand.toEuro2PaymentURI();

        ECKey.ECDSASignature ecdsaSignature = ethSignatureService.parseHexSignature(euro2PaymentURI.getSignature());
        String uriWithoutSignature = createPaymentRequestCommand.getEuro2PaymentUri().replace("&signature=" + euro2PaymentURI.getSignature(), "");
        byte[] rawHashOfUriWithoutSignature = ethSignatureService.rawHashSignableMessage(uriWithoutSignature);

        String requestorAddress;

        try {
            ethSignatureService.verifySignature(rawHashOfUriWithoutSignature, ecdsaSignature);
            requestorAddress = ethSignatureService.obtainEthAddressFromSignature(rawHashOfUriWithoutSignature, ecdsaSignature);
        } catch (Exception e) {
            throw new RuntimeException("Could not verify signature or obtain address from it");
        }

        PaymentRequest paymentRequest = PaymentRequest
            .builder()
            .requestorAddress(requestorAddress)
            .receiver(euro2PaymentURI.getAddress())
            .payer(euro2PaymentURI.getPayer())
            .euro2PaymentUri(createPaymentRequestCommand.getEuro2PaymentUri())
            .build();

        paymentRequestRepository.save(paymentRequest);

        return new ResponseEntity<String>(
            "Success",
            new HttpHeaders(), HttpStatus.OK);
    }

}
