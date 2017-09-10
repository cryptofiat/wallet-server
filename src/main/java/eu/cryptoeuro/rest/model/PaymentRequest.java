package eu.cryptoeuro.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
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
    String payer;
    @NotEmpty
    String receiver;

    @Tolerate
    PaymentRequest() {}
}
