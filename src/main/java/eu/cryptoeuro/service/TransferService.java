package eu.cryptoeuro.service;

import eu.cryptoeuro.dao.TransferRepository;
import eu.cryptoeuro.domain.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    public Transfer save(Transfer transfer){
        return transferRepository.save(transfer);
    }

    public Transfer get(Optional<Long> id){
        return transferRepository.findOne(id.orElse(null));
    }

}
