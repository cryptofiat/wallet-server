package eu.cryptoeuro.rest.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Builder
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    String euro2PaymentUri;
    @NotEmpty
    String requestorAddress;
    String payerAddress;
    @NotEmpty
    String receiverAddress;
}
