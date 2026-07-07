package com.mgleska.mmcqjava2.customer.action.command;

import com.mgleska.mmcqjava2.customer.action.enums.CustomerStatusEnum;
import com.mgleska.mmcqjava2.customer.model.Customer;
import com.mgleska.mmcqjava2.customer.model.CustomerRepository;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;

@Service
public class LoginCmd {

    private final CustomerRepository customerRepository;
    private final TokenService tokenService;

    public LoginCmd(CustomerRepository customerRepository, TokenService tokenService) {
        this.customerRepository = customerRepository;
        this.tokenService = tokenService;
    }

    public record ParamDto(
        @NotNull @PositiveOrZero int customerId
    ) {}

    public record ResultDto(
        @NotBlank String token
    ) {}

    public ResultDto handle(ParamDto dto) throws AppValidationException {
        Customer customer;
        if (dto.customerId() == 0) {
            customer = new Customer("Customer", CustomerStatusEnum.ACTIVE, 0);
            this.customerRepository.save(customer);
            customer.setName("Customer " + customer.getId());
            this.customerRepository.save(customer);
        }
        else {
            customer = this.customerRepository.findById(dto.customerId()).orElse(null);
            if (customer == null) {
                throw new AppValidationException("customerId", "Customer not found");
            }
        }

        return new ResultDto(tokenService.newAccessToken(customer.getId(), customer.getSelectedStore()));
    }
}
