package eu.cryptoeuro.service;

import eu.cryptoeuro.dao.PaymentRequestRepository;
import eu.cryptoeuro.rest.response.AccountPaymentRequestResponse;
import eu.cryptoeuro.rest.model.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class PaymentRequestService {
    @Autowired
    PaymentRequestRepository paymentRequestRepository;

    public List<AccountPaymentRequestResponse> findPaymentRequestsRelatedToAddress(String address) {
        List<AccountPaymentRequestResponse> paymentRequests = new ArrayList<>();

        for (PaymentRequest paymentRequest : paymentRequestRepository.findByRequestorAddress(address)) {
            paymentRequests.add(
                AccountPaymentRequestResponse.builder()
                .isRequesting(true)
                .adresseeAddress(paymentRequest.getAdresseeAddress())
                .requestorAddress(paymentRequest.getRequestorAddress())
                .build()
            );
        }

        for (PaymentRequest paymentRequest : paymentRequestRepository.findByAdresseeAddress(address)) {
            paymentRequests.add(
                AccountPaymentRequestResponse.builder()
                    .isRequesting(false)
                    .adresseeAddress(paymentRequest.getAdresseeAddress())
                    .requestorAddress(paymentRequest.getRequestorAddress())
                    .build()
            );
        }

        return paymentRequests;
    }
}
