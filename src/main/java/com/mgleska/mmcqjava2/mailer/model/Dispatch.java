package com.mgleska.mmcqjava2.mailer.model;

import com.mgleska.mmcqjava2.mailer.support.DispatchStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "mlr_dispatch")
@Getter
public class Dispatch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "ENUM('QUEUE', 'SENDING', 'SENT')", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private DispatchStatusEnum status = DispatchStatusEnum.QUEUE;

    @Column(nullable = false)
    @Length(max = 50)
    @Setter
    private String source;

    @Setter
    long sourceId;

    @Column(nullable = false)
    @Length(max = 500)
    @Setter
    private String recipient;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    @Setter
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Dispatch() {
        // for Hibernate
    }

    public Dispatch(String source, long sourceId, String recipient, String message) {
        this.source = source;
        this.sourceId = sourceId;
        this.recipient = recipient;
        this.message = message;
    }
}
