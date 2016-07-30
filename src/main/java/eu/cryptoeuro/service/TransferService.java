package eu.cryptoeuro.service;

import eu.cryptoeuro.dao.TransferRepository;
import eu.cryptoeuro.domain.Transfer;
import eu.cryptoeuro.domain.TransferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    public Transfer save(Transfer transfer){
        transfer.setStatus(TransferStatus.PENDING);
        return transferRepository.save(transfer);
    }

    public Transfer get(Optional<Long> id){
        return transferRepository.findOne(id.orElse(null));
    }

}
