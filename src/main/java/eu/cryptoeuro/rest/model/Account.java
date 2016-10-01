package eu.cryptoeuro.rest.model;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {

    @NotNull
    private boolean approved;

}
