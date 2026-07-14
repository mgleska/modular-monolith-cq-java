package com.mgleska.mmcqjava2.customer.action.command;

import com.mgleska.mmcqjava2.customer.action.enums.CustomerStatusEnum;
import com.mgleska.mmcqjava2.customer.model.Customer;
import com.mgleska.mmcqjava2.customer.model.CustomerRepository;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginCmdTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginCmd loginCmd;

    @Test
    void createsNewCustomerWhenCustomerIdIsZero() {
        when(tokenService.newAccessToken(0, 0)).thenReturn("new-token");

        var result = loginCmd.handle(new LoginCmd.ParamDto(0));

        assertThat(result.token()).isEqualTo("new-token");

        var captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository, times(2)).save(captor.capture());
        var savedCustomer = captor.getValue();
        assertThat(savedCustomer.getName()).isEqualTo("Customer 0");
        assertThat(savedCustomer.getStatus()).isEqualTo(CustomerStatusEnum.ACTIVE);
        assertThat(savedCustomer.getSelectedStore()).isZero();
    }

    @Test
    void returnsTokenForExistingCustomer() {
        var customer = new Customer("Existing", CustomerStatusEnum.ACTIVE, 3);
        ReflectionTestUtils.setField(customer, "id", 5);
        when(customerRepository.findById(5)).thenReturn(Optional.of(customer));
        when(tokenService.newAccessToken(5, 3)).thenReturn("existing-token");

        var result = loginCmd.handle(new LoginCmd.ParamDto(5));

        assertThat(result.token()).isEqualTo("existing-token");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void throwsWhenExistingCustomerNotFound() {
        when(customerRepository.findById(7)).thenReturn(Optional.empty());
        var params = new LoginCmd.ParamDto(7);

        assertThatThrownBy(() -> loginCmd.handle(params))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("customerId"));

        verify(customerRepository, never()).save(any());
        verifyNoInteractions(tokenService);
    }
}
