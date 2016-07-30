package eu.cryptoeuro.domain;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    @Size(min = 1, max = 256)
    private String sourceAccount;
    @Size(min = 1, max = 256)
    private String targetAccount;
    private TransferStatus status;
    private Date created;
    private Date updated;
    @NotNull
    @Min(1)
    private Long amount;
    @Size(min = 1, max = 256)
    private String reference;

    @PrePersist
    void created() {
        this.created = this.updated = new Date();
    }

    @PreUpdate
    void updated() {
        this.updated = new Date();
    }
}
