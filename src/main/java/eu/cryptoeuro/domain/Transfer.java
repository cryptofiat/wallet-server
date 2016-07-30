package eu.cryptoeuro.domain;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String targetAccount;
    private TransferStatus status;
    private Date created;
    private Date updated;

    @PrePersist
    void created() {
        this.created = this.updated = new Date();
    }

    @PreUpdate
    void updated() {
        this.updated = new Date();
    }
}
