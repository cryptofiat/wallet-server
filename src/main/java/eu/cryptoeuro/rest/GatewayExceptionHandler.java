package eu.cryptoeuro.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Errors> handleServiceException(ValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<Errors>(ex.getErrors(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
