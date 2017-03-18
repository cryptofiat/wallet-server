package eu.cryptoeuro.rest;

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

import eu.cryptoeuro.rest.model.Balance;
import eu.cryptoeuro.service.ReserveService;

@Api(value="reserve",
        description="System info.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/reserve")
@Slf4j
@CrossOrigin(origins = "*")
public class ReserveController {

    @Autowired
    private ReserveService reserveService;

    @ApiOperation(value = "Get total amount of money in bank reserve.")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<Balance> getTotalReserve(){
        log.info("Getting total amount of money in bank reserve");
        Balance balance = reserveService.getTotalReserve();

        return new ResponseEntity<>(
                balance,
                new HttpHeaders(), HttpStatus.OK);
    }
}
