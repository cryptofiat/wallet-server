package eu.cryptoeuro.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Optional;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.service.BalanceService;

@Api(value="balances",
        description="This is an object representing your balance. You can retrieve it to see the balance currently on your account.\n" +
                "You can also retrieve a list of the balance history, which contains a list of transactions that contributed to the balance.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/balances")
@Slf4j
public class BalanceController {

    @Autowired
    BalanceService balanceService;

    @ApiOperation(value = "Get account ether balance.")
    @RequestMapping(method = RequestMethod.GET, value = "/ether")
    public ResponseEntity<Balance> getEtherumBalance(
            @Valid @RequestParam(value = "account", required = false) Optional<String> account
    ){
        log.info("Getting Ether balance for account " + account.toString());
        return new ResponseEntity<Balance>(
                balanceService.getEtherBalance(account),
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account balance.")
    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ResponseEntity<Long> getBalance(
            @Valid @RequestParam(value = "account", required = true) String account
            ){
        log.info("Getting balance for account " + account.toString());
        return new ResponseEntity<>(
                balanceService.getBalance(account),
                new HttpHeaders(), HttpStatus.OK);
    }

}
