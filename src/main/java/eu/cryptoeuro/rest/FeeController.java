package eu.cryptoeuro.rest;

import eu.cryptoeuro.FeeConstant;
import eu.cryptoeuro.rest.model.Fee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Api(value="fees",
        description="Fees",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/fees")
@Slf4j
public class FeeController {
    @ApiOperation(value = "Get fee.")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Fee> getFee(){
        log.info("Getting fee: " + FeeConstant.FEE);
        return new ResponseEntity<>(
                new Fee(),
                new HttpHeaders(), HttpStatus.OK);
    }

}
