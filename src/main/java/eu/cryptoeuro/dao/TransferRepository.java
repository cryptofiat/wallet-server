package eu.cryptoeuro.dao;

import eu.cryptoeuro.domain.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "Transfers", path = "transfers")
public interface TransferRepository extends CrudRepository<Transfer, Long> {

}
