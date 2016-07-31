package eu.cryptoeuro.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleServiceException(ValidationException ex) {
        log.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getErrors().getAllErrors(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
