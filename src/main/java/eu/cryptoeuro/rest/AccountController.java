package eu.cryptoeuro.rest;

import eu.cryptoeuro.rest.model.Account;
import eu.cryptoeuro.service.AccountService;
import eu.cryptoeuro.service.BalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value="accounts",
        description="Accounts info.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/accounts")
@Slf4j
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    BalanceService balanceService;

    @ApiOperation(value = "Get account.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountAddress){
        log.info("Getting account " + accountAddress.toString());
        Account account = new Account(accountService.isApproved(accountAddress));
        account.setBalance(balanceService.getBalance(accountAddress));

        log.info("Getting account " + account.toString() + " " + account);

        return new ResponseEntity<>(
                account,
                new HttpHeaders(), HttpStatus.OK);
    }

}
