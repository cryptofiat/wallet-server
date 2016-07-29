package eu.cryptoeuro.rest;

import eu.cryptoeuro.rest.model.Balance;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@Api(value="balances",
        description="This is an object representing your balance. You can retrieve it to see the balance currently on your account.\n" +
                "You can also retrieve a list of the balance history, which contains a list of transactions that contributed to the balance.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/balances")
@Slf4j
public class BalanceController {

    @ApiOperation(value = "Get account balance.")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Balance> getBalance(
            @Valid @RequestParam(value = "account", required = false) String account
    ){
        return new ResponseEntity<Balance>(
                new Balance(new BigDecimal(1000), "transferId"),
                new HttpHeaders(), HttpStatus.OK);
    }
}
