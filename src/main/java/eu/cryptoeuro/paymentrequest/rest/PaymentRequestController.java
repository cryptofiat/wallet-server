package eu.cryptoeuro.paymentrequest.rest;

import eu.cryptoeuro.dao.PaymentRequestRepository;
import eu.cryptoeuro.euro2paymenturi.Euro2PaymentURI;
import eu.cryptoeuro.paymentrequest.rest.command.CreatePaymentRequestCommand;
import eu.cryptoeuro.rest.exception.ValidationException;
import eu.cryptoeuro.rest.model.PaymentRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.crypto.ECKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

@RestController
@RequestMapping("/v1/payment-request")
@Slf4j
public class PaymentRequestController {

    @Autowired
    PaymentRequestRepository paymentRequestRepository;

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
        euro2PaymentURI.getAddress();

        String signatureString = euro2PaymentURI.getSignature();
        byte[] signatureBytes = Hex.decode(signatureString);
        ECKey.ECDSASignature ecdsaSignature = ECKey.ECDSASignature.decodeFromDER(signatureBytes);
        String uriWithoutSignature = createPaymentRequestCommand.getEuro2PaymentUri().replace("?signature=" + euro2PaymentURI.getSignature(), "");
        byte[] uriWithoutSignatureRawHash = uriWithoutSignature.getBytes(StandardCharsets.UTF_8);

        try {
            if (!ECKey.signatureToKey(uriWithoutSignatureRawHash, ecdsaSignature).verify(uriWithoutSignatureRawHash, ecdsaSignature)) throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid euro payment uri signature");
        }
        byte[] requestorAddressByteArray = ECKey.recoverAddressFromSignature(0, ecdsaSignature, uriWithoutSignatureRawHash);


        PaymentRequest paymentRequest = PaymentRequest
            .builder()
            .requestorAddress(Hex.toHexString(requestorAddressByteArray))
            .receiverAddress(euro2PaymentURI.getAddress())
            .payerAddress(euro2PaymentURI.getPayer())
            .euro2PaymentUri(createPaymentRequestCommand.getEuro2PaymentUri())
            .build();

        paymentRequestRepository.save(paymentRequest);

        return new ResponseEntity<String>(
            "Success",
            new HttpHeaders(), HttpStatus.OK);
    }

}
