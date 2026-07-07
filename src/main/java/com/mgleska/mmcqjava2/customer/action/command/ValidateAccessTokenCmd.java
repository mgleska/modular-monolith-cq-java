package com.mgleska.mmcqjava2.customer.action.command;

import com.mgleska.mmcqjava2.customer.support.CustomerAuthenticationToken;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.springframework.stereotype.Service;

@Service
public class ValidateAccessTokenCmd {

    private final TokenService tokenService;

    public ValidateAccessTokenCmd(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public CustomerAuthenticationToken validate(String jwt) {

        var map = this.tokenService.decodeAccessToken(jwt);
        if (! map.containsKey("uid")) {
            throw new AppNeverException("Missing 'uid' in JWT token");
        }

        return new CustomerAuthenticationToken(map.get("uid").asInt(), map.containsKey("stid") ? map.get("stid").asInt() : 0);
    }

}
