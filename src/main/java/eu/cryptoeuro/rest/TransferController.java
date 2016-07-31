package eu.cryptoeuro.rest;

import eu.cryptoeuro.rest.exception.ValidationException;
import eu.cryptoeuro.rest.model.Transfer;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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

    @ApiOperation(value = "Get all transfer.")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Iterable<Transfer>> getTransfer(){
        log.info("Getting all transfers");

        return new ResponseEntity<>(
                transferService.getAll(),
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get transfer.")
    @RequestMapping(method = RequestMethod.GET, value = "/{transferId}")
    public ResponseEntity<Transfer> getTransfer(
            @PathVariable Long transferId
    ){
        log.info("Getting a transfer " + transferId);

        return new ResponseEntity<>(
                transferService.get(transferId),
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a transfer")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<Transfer> postWithTransferRequest(
            @Valid @RequestBody @ApiParam CreateTransferCommand createTransferCommand,
            @ApiIgnore Errors errors
    ) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        return new ResponseEntity<>(
                transferService.save(createTransferCommand),
                new HttpHeaders(), HttpStatus.OK);

    }

    @ApiOperation(value = "Test call sent transaction.")
    @RequestMapping(method = RequestMethod.POST, value = "/testSendTransaction")
    public ResponseEntity<String> postTestSendTransaction(
            @Valid @RequestParam(value = "account", required = false) Optional<String> account,
            @Valid @RequestParam(value = "amount", required = false) Optional<Long> amount
    ){
        log.info("Test send transaction for account " + account.toString() + ", " + amount.toString());
        return new ResponseEntity<>(
                transferService.sendTransaction(account, amount),
                new HttpHeaders(), HttpStatus.OK);
    }


}
