package com.mgleska.mmcqjava2.customer.action.command;

import com.mgleska.mmcqjava2.customer.model.Customer;
import com.mgleska.mmcqjava2.customer.model.CustomerRepository;
import com.mgleska.mmcqjava2.customer.support.CustomerAuthenticationToken;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.action.query.CheckStoreExistsQry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeStoreCmdTest {

    private static final int CUSTOMER_ID = 12;
    private static final int STORE_ID = 2;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CheckStoreExistsQry checkStoreExistsQry;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ChangeStoreCmd changeStoreCmd;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void throwsWhenStoreDoesNotExist() {
        when(checkStoreExistsQry.check(STORE_ID)).thenReturn(false);
        var params = new ChangeStoreCmd.ParamDto(STORE_ID);

        assertThatThrownBy(() -> changeStoreCmd.handle(params))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("storeId"));

        verifyNoInteractions(customerRepository, tokenService);
    }

    @Test
    void throwsWhenCustomerNotAuthenticated() {
        when(checkStoreExistsQry.check(STORE_ID)).thenReturn(true);
        SecurityContextHolder.clearContext();
        var params = new ChangeStoreCmd.ParamDto(STORE_ID);

        assertThatThrownBy(() -> changeStoreCmd.handle(params))
            .isInstanceOf(AppNeverException.class);

        verifyNoInteractions(customerRepository, tokenService);
    }

    @Test
    void throwsWhenCustomerNotFound() {
        when(checkStoreExistsQry.check(STORE_ID)).thenReturn(true);
        authenticateAs(CUSTOMER_ID, 0);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
        var params = new ChangeStoreCmd.ParamDto(STORE_ID);

        assertThatThrownBy(() -> changeStoreCmd.handle(params))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("jwt.uid"));

        verify(customerRepository, never()).save(any());
        verifyNoInteractions(tokenService);
    }

    @Test
    void updatesSelectedStoreAndReturnsNewToken() {
        when(checkStoreExistsQry.check(STORE_ID)).thenReturn(true);
        authenticateAs(CUSTOMER_ID, 0);
        var customer = new Customer("Jane", com.mgleska.mmcqjava2.customer.action.enums.CustomerStatusEnum.ACTIVE, 0);
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(tokenService.newAccessToken(CUSTOMER_ID, STORE_ID)).thenReturn("new-token");
        var params = new ChangeStoreCmd.ParamDto(STORE_ID);

        var result = changeStoreCmd.handle(params);

        assertThat(result.token()).isEqualTo("new-token");
        assertThat(customer.getSelectedStore()).isEqualTo(STORE_ID);
        verify(customerRepository).save(customer);
    }

    private void authenticateAs(int customerId, int storeId) {
        SecurityContextHolder.getContext().setAuthentication(new CustomerAuthenticationToken(customerId, storeId));
    }
}
