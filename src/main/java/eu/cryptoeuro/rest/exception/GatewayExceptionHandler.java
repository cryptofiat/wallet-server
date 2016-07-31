package eu.cryptoeuro.rest.exception;

import eu.cryptoeuro.service.exception.AccountNotApprovedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GatewayExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleServiceException(ValidationException ex) {
        log.info(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getErrors().getAllErrors(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotApprovedException.class)
    public ResponseEntity<Object> handleServiceException(AccountNotApprovedException ex) {
        log.info(ex.getMessage(), ex);
        return new ResponseEntity<>(errorMap(ex.getMessage()), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    private Map errorMap(String message) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", message);
        return errorMap;
    }

}
