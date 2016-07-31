package eu.cryptoeuro.rest.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Account {
    @NotNull
    boolean approved;
    Balance balance;

    public Account(boolean approved) {
        this.approved = approved;
    }
}
