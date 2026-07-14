package com.mgleska.mmcqjava2.customer.action.command;

import com.mgleska.mmcqjava2.customer.support.CustomerAuthenticationToken;
import com.mgleska.mmcqjava2.customer.model.CustomerRepository;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.action.query.CheckStoreExistsQry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ChangeStoreCmd {

    private final CustomerRepository customerRepository;
    private final CheckStoreExistsQry checkStoreExistsQry;
    private final TokenService tokenService;

    public ChangeStoreCmd(CustomerRepository customerRepository, CheckStoreExistsQry checkStoreExistsQry, TokenService tokenService) {
        this.customerRepository = customerRepository;
        this.checkStoreExistsQry = checkStoreExistsQry;
        this.tokenService = tokenService;
    }

    public record ParamDto(
        @NotNull @Positive int storeId
    ) {
    }

    public record ResultDto(
        @NotBlank String token
    ) {
    }

    public ResultDto handle(ParamDto params) throws AppValidationException {

        if (! this.checkStoreExistsQry.check(params.storeId)) {
            throw new AppValidationException("storeId", "Invalid store id");
        }

        var authToken = (CustomerAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authToken == null) {
            throw new AppNeverException("Customer is not authenticated.");
        }

        var customer = this.customerRepository.findById(authToken.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new AppValidationException("jwt.uid", "Customer not found.");
        }

        customer.setSelectedStore(params.storeId);
        customerRepository.save(customer);

        return new ResultDto(this.tokenService.newAccessToken(authToken.getCustomerId(), params.storeId));
    }
}
