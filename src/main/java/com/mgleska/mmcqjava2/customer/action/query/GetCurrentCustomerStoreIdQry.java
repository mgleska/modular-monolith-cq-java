package com.mgleska.mmcqjava2.customer.action.query;

import com.mgleska.mmcqjava2.customer.support.CustomerAuthenticationToken;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentCustomerStoreIdQry {

    public int handle() {

        var authToken = (CustomerAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (authToken == null) {
            throw new AppNeverException("Customer is not authenticated.");
        }

        return authToken.getStoreId();
    }
}
