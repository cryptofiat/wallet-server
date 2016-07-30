package eu.cryptoeuro.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String targetAccount;
    private Date created;
    private Date updated;
    private TransferStatus status;

    @PrePersist
    void created() {
        this.created = this.updated = new Date();
    }

    @PreUpdate
    void updated() {
        this.updated = new Date();
    }
}
