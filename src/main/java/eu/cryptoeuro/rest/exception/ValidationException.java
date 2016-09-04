package eu.cryptoeuro.rest.exception;

import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {

    @NotNull
    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
	if (errors.hasErrors()) {
		log.info("validation throwing error:" + errors);
	}
    }

    Errors getErrors() {
        return this.errors;
    }
}
