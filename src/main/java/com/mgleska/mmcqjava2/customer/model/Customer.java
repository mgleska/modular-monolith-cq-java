package com.mgleska.mmcqjava2.customer.model;

import com.mgleska.mmcqjava2.customer.action.enums.CustomerStatusEnum;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "cst_customer")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int selectedStore = 0;

    @Column(columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'DEACTIVATING')", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatusEnum status = CustomerStatusEnum.ACTIVE;

    @Column(nullable = false)
    @Length(max = 250)
    private String name;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Customer() {
        // for Hibernate
    }

    public Customer(String name, CustomerStatusEnum status, int selectedStore) {
        this.name = name;
        this.status = status;
        this.selectedStore = selectedStore;
    }

    public int getId() {
        return id;
    }

    public int getSelectedStore() {
        return selectedStore;
    }

    public void setSelectedStore(int selectedStore) {
        this.selectedStore = selectedStore;
    }

    public CustomerStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CustomerStatusEnum status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
