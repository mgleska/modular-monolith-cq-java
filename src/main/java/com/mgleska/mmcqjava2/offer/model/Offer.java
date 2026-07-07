package com.mgleska.mmcqjava2.offer.model;

import com.mgleska.mmcqjava2.shared.Constants;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "ofr_offer", uniqueConstraints = {@UniqueConstraint(columnNames = {"storeId", "productEan"})})
public class Offer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int version = 0;

    @Column(nullable = false)
    private int storeId;

    @Column(nullable = false)
    @Length(max = Constants.OFFER_EXTERNAL_ID_MAX_LENGTH)
    private String externalId;

    @Column(nullable = false)
    @Length(max = Constants.EAN_MAX_LENGTH)
    private String productEan;

    @Column(nullable = true)
    @Length(max = 200)
    private String productName = null;

    @Column(nullable = false)
    private int price;

    @Column(nullable = true)
    private Integer lowestPrice = null;

    @Column(nullable = false)
    @ColumnDefault("1")
    private boolean visible = true;

    @Column(nullable = true)
    private Integer productId = null;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Offer() {
        // for Hibernate
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getProductEan() {
        return productEan;
    }

    public void setProductEan(String productEan) {
        this.productEan = productEan;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Integer getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(Integer lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
