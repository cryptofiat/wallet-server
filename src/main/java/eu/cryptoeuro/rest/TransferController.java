package eu.cryptoeuro.rest;

import eu.cryptoeuro.domain.Transfer;
import eu.cryptoeuro.rest.command.CreateTransferCommand;
import eu.cryptoeuro.service.TransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Api(value="transfers",
        description="transfers desc",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/transfers")
@Slf4j
public class TransferController {

    @Autowired
    TransferService transferService;

    @ApiOperation(value = "Get transfer.")
    @RequestMapping(method = RequestMethod.GET, value = "/{transferId}")
    public ResponseEntity<Transfer> getTransfer(
            @PathVariable Optional<Long> transferId
    ){
        log.info("Getting a transfer " + transferId);

        return new ResponseEntity<>(
                transferService.get(transferId),
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a transfer")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<Transfer> postWithTransferRequest(@Valid @RequestBody @ApiParam CreateTransferCommand createTransferCommand) {
        eu.cryptoeuro.domain.Transfer transfer = new eu.cryptoeuro.domain.Transfer();
        transfer.setTargetAccount(createTransferCommand.getTargetAccount());

        return new ResponseEntity<>(
                transferService.save(transfer),
                new HttpHeaders(), HttpStatus.OK);

    }


}
