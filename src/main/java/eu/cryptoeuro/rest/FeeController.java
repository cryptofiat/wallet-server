package eu.cryptoeuro.rest;

import eu.cryptoeuro.rest.model.GasPrice;
import eu.cryptoeuro.service.GasPriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.cryptoeuro.FeeConstant;
import eu.cryptoeuro.rest.model.Fee;

@Api(value="fees",
        description="Fees",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/fees")
@Slf4j
@CrossOrigin(origins = "*")
public class FeeController {

    @Autowired
    private GasPriceService gasPriceService;

    @ApiOperation(value = "Get fee.")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Fee> getFee(){
        log.info("Getting fee: " + FeeConstant.FEE);
        return new ResponseEntity<>(
                new Fee(),
                new HttpHeaders(), HttpStatus.OK);

    }

    @ApiOperation(value = "Get gas price.")
    @RequestMapping(method = RequestMethod.GET, value = "/gasPrice")
    public ResponseEntity<GasPrice> getGasPrice() {
        Long gasPriceWei = gasPriceService.getGasPriceInWei();
        log.info("Getting gas price (wei): " + gasPriceWei);
        return new ResponseEntity<>(
            new GasPrice(gasPriceWei),
            new HttpHeaders(),
            HttpStatus.OK
        );
    }
}
