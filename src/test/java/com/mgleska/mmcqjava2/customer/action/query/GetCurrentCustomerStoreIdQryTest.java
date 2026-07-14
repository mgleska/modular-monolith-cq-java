package com.mgleska.mmcqjava2.customer.action.query;

import com.mgleska.mmcqjava2.customer.support.CustomerAuthenticationToken;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetCurrentCustomerStoreIdQryTest {

    private final GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry = new GetCurrentCustomerStoreIdQry();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsStoreIdOfAuthenticatedCustomer() {
        SecurityContextHolder.getContext().setAuthentication(new CustomerAuthenticationToken(12, 3));

        assertThat(getCurrentCustomerStoreIdQry.handle()).isEqualTo(3);
    }

    @Test
    void throwsWhenCustomerNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(getCurrentCustomerStoreIdQry::handle)
            .isInstanceOf(AppNeverException.class);
    }
}
