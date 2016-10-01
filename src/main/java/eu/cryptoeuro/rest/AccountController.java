package eu.cryptoeuro.rest;

import eu.cryptoeuro.service.TransferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.cryptoeuro.rest.model.Account;
import eu.cryptoeuro.service.AccountService;

@Api(value="accounts",
        description="Accounts info.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/accounts")
@Slf4j
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransferService transferService;

    @ApiOperation(value = "Get account.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountAddress){
        log.info("Getting account " + accountAddress.toString());
        Account account = new Account(accountService.isApproved(accountAddress));

        log.info("Getting account " + account.toString() + " " + account);

        return new ResponseEntity<>(
                account,
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/transfers")
    public ResponseEntity<Object> getAccountTransfers(@PathVariable String accountAddress){
        log.info("Getting account transfers " + accountAddress.toString());
        String transfers = transferService.getTransfersForAccount(accountAddress);

        log.info("Getting account transfers " + transfers.toString());

        return new ResponseEntity<>(
                transfers,
                new HttpHeaders(), HttpStatus.OK);
    }


}
