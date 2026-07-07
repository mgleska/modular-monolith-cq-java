package com.mgleska.mmcqjava2.customer.support;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final int customerId;
    private final int storeId;

    public CustomerAuthenticationToken(int customerId, int storeId) {
        super(new Object(), null, new ArrayList<>());
        this.customerId = customerId;
        this.storeId = storeId;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public int getStoreId() {
        return this.storeId;
    }

    @Override
    @NullMarked
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomerAuthenticationToken other)) {
            return false;
        }

        return customerId == other.customerId && storeId == other.storeId && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customerId, storeId);
    }
}
