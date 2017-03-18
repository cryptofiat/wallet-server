package eu.cryptoeuro.rest;

import eu.cryptoeuro.paymentrequest.rest.response.AccountPaymentRequestResponse;
import eu.cryptoeuro.rest.model.*;
import eu.cryptoeuro.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
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
    private BalanceService balanceService;
    @Autowired
    private NonceService nonceService;
    @Autowired
    private TransferService transferService;
    @Autowired
    PaymentRequestService paymentRequestService;

    @ApiOperation(value = "Get account.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountAddress){
        log.info("Getting account " + accountAddress);
        Account account = new Account(
                accountAddress,
                accountService.isApproved(accountAddress),
                accountService.isClosed(accountAddress),
                accountService.isFrozen(accountAddress),
                nonceService.getDelegatedNonceOf(accountAddress).getNonce(),
                balanceService.getBalance(accountAddress).getAmount()
                );
        log.info("Statuses for account " + accountAddress + " -> " + account);

        return new ResponseEntity<>(
                account,
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account transfers.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/transfers")
    public ResponseEntity<List> getAccountTransfers(@PathVariable String accountAddress){
        log.info("Getting account transfers " + accountAddress.toString());
        List<Transfer> transfers = transferService.getTransfersForAccount(accountAddress);

        log.info("Getting account transfers " + transfers.toString());

        return new ResponseEntity<>(
                transfers,
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account payment requests.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/requests")
    public ResponseEntity<List<AccountPaymentRequestResponse>> getAccountPaymentRequests(@PathVariable String accountAddress){
        log.info("Getting account payment requests " + accountAddress.toString());
        List<AccountPaymentRequestResponse> paymentRequests = paymentRequestService.findPaymentRequestsRelatedToAddress(accountAddress);
        log.info("Getting account requests: " + paymentRequests.size());
        return new ResponseEntity<>(
            paymentRequests,
            new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account ether balance.")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/balance/eth")
    public ResponseEntity<Balance> getAccountEthBalance(@PathVariable String accountAddress){
        log.info("Getting Ether balance for account " + accountAddress.toString());
        Balance balance = balanceService.getEtherBalance(accountAddress);

        return new ResponseEntity<>(
                balance,
                new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get account's current delegatedNonceOf value (you need to increment it yourself for the next transfer).")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}/nonce")
    public ResponseEntity<Nonce> getNonce(@PathVariable String accountAddress){
        log.info("Getting DelegatedNonceOf for account " + accountAddress.toString());
        Nonce nonce = nonceService.getDelegatedNonceOf(accountAddress);

        return new ResponseEntity<>(nonce, new HttpHeaders(), HttpStatus.OK);
    }

}
