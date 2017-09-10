package eu.cryptoeuro.dao;

import eu.cryptoeuro.rest.model.PaymentRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRequestRepository extends CrudRepository<PaymentRequest,Long> {
    List<PaymentRequest> findByRequestorAddress(String requestorAddress);
}
