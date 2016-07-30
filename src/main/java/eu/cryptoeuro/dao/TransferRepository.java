package eu.cryptoeuro.dao;

import eu.cryptoeuro.domain.Transfer;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends CrudRepository<Transfer, Long> {

}
