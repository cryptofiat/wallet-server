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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiParam;

import eu.cryptoeuro.rest.command.NotifyTransferCommand;
import eu.cryptoeuro.service.PushNotificationService;

@Api(value="push",
        description="Push notifications to clients.",
        produces = "application/json", consumes = "application/json")
@RestController
@RequestMapping("/v1/push")
@Slf4j
@CrossOrigin(origins = "*")
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushService;

    @ApiOperation(value = "Push transfer notification to apps listening on the receivers address.")
    @RequestMapping(method = RequestMethod.POST, value = "transfer")
    public ResponseEntity<String> pushTransfer( @RequestBody @ApiParam NotifyTransferCommand cmd){

	//TODO: add command validation
	//TODO: return the {name: code} firebase response

        pushService.pushNotifyTransfer(cmd);

        return new ResponseEntity<String>(
                "",
                new HttpHeaders(), HttpStatus.OK);
    }
}
