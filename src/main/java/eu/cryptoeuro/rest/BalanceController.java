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
import org.springframework.web.bind.annotation.*;

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.service.BalanceService;

@Api(value="balances",
        description="This is an object representing your balance. You can retrieve it to see the balance currently on your account.\n" +
                "You can also retrieve a list of the balance history, which contains a list of transactions that contributed to the balance.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/balances")
@Slf4j
@CrossOrigin(origins = "*")
public class BalanceController {

    @Autowired
    BalanceService balanceService;

    @ApiOperation(value = "Get account ether balance.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/eth")
    public ResponseEntity<Balance> getEthBalance(@PathVariable String accountAddress){
        log.info("Getting balance for ETH account " + accountAddress.toString());
        Balance balance= balanceService.getEtherBalance(accountAddress);
        log.info("Getting balance for ETH account " + accountAddress.toString() + " " + balance);

        return new ResponseEntity<>(
                balance,
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account balance.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}")
    public ResponseEntity<Balance> getBalance(@PathVariable String accountAddress){
        log.info("Getting balance for EUR_CENT account " + accountAddress.toString());
        Balance balance= balanceService.getBalance(accountAddress);
        log.info("Getting balance for EUR_CENT account " + accountAddress.toString() + " " + balance);

        return new ResponseEntity<>(
                balance,
                new HttpHeaders(), HttpStatus.OK);
    }

}
