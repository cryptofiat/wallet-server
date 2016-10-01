package eu.cryptoeuro.rest.exception;

import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.Errors;

@Slf4j
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    	if (errors.hasErrors()) {
    		log.info("validation throwing error: " + errors);
    	}
    }

    public Errors getErrors() {
        return errors;
    }

}
