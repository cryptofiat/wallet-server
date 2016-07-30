package eu.cryptoeuro.service;

import eu.cryptoeuro.dao.TransferRepository;
import eu.cryptoeuro.domain.Transfer;
import eu.cryptoeuro.domain.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    public Transfer save(Transfer transfer){
        transfer.setStatus(TransferStatus.PENDING);
        transfer = transferRepository.save(transfer);
        final Long transferId = transfer.getId();

        Runnable executeTransfer = () -> {
            try {
                executeTransfer(transferId);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(executeTransfer);
        thread.start();

        return transfer;
    }

    public Transfer get(Long id){
        return transferRepository.findOne(id);
    }

    public Iterable<Transfer> getAll(){
        return transferRepository.findAll();
    }

    private void executeTransfer(Long transferId) throws InterruptedException {

        //TODO: call transfer creation

        String name = Thread.currentThread().getName();
        TimeUnit.SECONDS.sleep(10);
        log.info("Bar " + name);

        Transfer transfer = transferRepository.findOne(transferId);
        transfer.setStatus(TransferStatus.SUCCESSFUL);
        transferRepository.save(transfer);
    }


}
