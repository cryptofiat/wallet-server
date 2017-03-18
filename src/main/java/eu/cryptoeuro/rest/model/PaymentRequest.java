package eu.cryptoeuro.rest.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class PaymentRequest {

	@Id
	Long id;
}
