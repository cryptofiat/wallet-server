package eu.cryptoeuro.rest.command;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Data
public class CreateTransferCommand {
    @NotNull
    @Size(min = 1, max = 256)
    private String sourceAccount;
    @NotNull
    @Size(min = 1, max = 256)
    private String targetAccount;
    @NotNull
    @Min(1)
    private Long amount;
    @Min(0)
    private Long reference;

    @NotNull
    Long v;
    @NotNull
    String r;
    @NotNull
    String s;

    public Optional<Long> getReference() {
        return Optional.ofNullable(reference);
    }

}
