package com.mgleska.mmcqjava2.user.action.command;

import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import com.mgleska.mmcqjava2.user.enums.UserStatusEnum;
import com.mgleska.mmcqjava2.user.support.UserTokenService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class ValidateUserTokenCmd {

    private final UserTokenService tokenService;

    public ValidateUserTokenCmd(UserTokenService tokenService) {
        this.tokenService = tokenService;
    }

    public UsernamePasswordAuthenticationToken validate(String token) {
        var user = tokenService.validateToken(token);

        if (user.getStatus() != UserStatusEnum.ACTIVE) {
            throw new AppAuthException("User is not active.");
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
