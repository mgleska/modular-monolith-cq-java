package com.mgleska.mmcqjava2.store.model;

import com.mgleska.mmcqjava2.shared.Constants;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = Store.TABLE, indexes = @Index(name = "deleted", columnList = "is_deleted"))
@SoftDelete(columnName = "is_deleted")
public class Store implements Serializable {

    public static final String TABLE = "str_store";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Length(max = Constants.STORE_EXTERNAL_ID_MAX_LENGTH)
    private String externalId;

    @Column(nullable = false)
    @Length(max = 250)
    private String name;

    @Column(nullable = false)
    @Length(max = 250)
    private String address;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Store() {
        // for Hibernate
    }

    public Store(String externalId, String name, String address) {
        this.externalId = externalId;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
