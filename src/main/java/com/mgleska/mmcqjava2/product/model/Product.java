package com.mgleska.mmcqjava2.product.model;

import com.mgleska.mmcqjava2.shared.Constants;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "prd_product", indexes = {@Index(columnList = "ean")})
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Length(max = Constants.EAN_MAX_LENGTH)
    private String ean;

    @Column(nullable = false)
    @Length(max = 200)
    private String name;

    @Column(nullable = true)
    @Length(max = Constants.URL_MAX_LENGTH)
    private String imageUrl = null;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Product() {
        // for Hibernate
    }

    public int getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
