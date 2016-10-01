package eu.cryptoeuro.rest;

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

import eu.cryptoeuro.rest.model.Nonce;
import eu.cryptoeuro.service.NonceService;

@Api(value="nonces",
        description="Account's delegatedTransferNonce protects against using same signed transfer multiple times",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/nonces")
@Slf4j
@CrossOrigin(origins = "*")
public class NonceController {

    @Autowired
    private NonceService nonceService;

    @ApiOperation(value = "Get account's current delegatedTransferNonce value (you need to increment it yourself for the next transfer).")
    @RequestMapping(method = RequestMethod.GET, value = "/{accountAddress}")
    public ResponseEntity<Nonce> getNonce(@PathVariable String accountAddress){
        log.info("Getting delegatedTransferNonce for account " + accountAddress.toString());
        Nonce nonce = nonceService.getDelegatedTransferNonce(accountAddress);

        return new ResponseEntity<>(nonce, new HttpHeaders(), HttpStatus.OK);
    }

}
