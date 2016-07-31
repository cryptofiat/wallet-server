package eu.cryptoeuro.dao;

import eu.cryptoeuro.rest.model.Transfer;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends CrudRepository<Transfer, Long> {

}
