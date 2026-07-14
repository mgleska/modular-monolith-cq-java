package com.mgleska.mmcqjava2.user.action.command;

import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import com.mgleska.mmcqjava2.user.enums.RoleEnum;
import com.mgleska.mmcqjava2.user.enums.UserStatusEnum;
import com.mgleska.mmcqjava2.user.model.User;
import com.mgleska.mmcqjava2.user.support.UserTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateUserTokenCmdTest {

    @Mock
    private UserTokenService tokenService;

    @InjectMocks
    private ValidateUserTokenCmd validateUserTokenCmd;

    private User buildUser(UserStatusEnum status) {
        var user = new User();
        user.setEmail("user@example.com");
        user.setStatus(status);
        user.setRoles(Set.of(RoleEnum.ROLE_USER));
        return user;
    }

    @Test
    void returnsAuthenticationTokenForActiveUser() {
        var user = buildUser(UserStatusEnum.ACTIVE);
        when(tokenService.validateToken("token")).thenReturn(user);

        var result = validateUserTokenCmd.validate("token");

        assertThat(result.getPrincipal()).isEqualTo(user);
        assertThat(result.getCredentials()).isNull();
        assertThat(result.getAuthorities()).containsExactlyInAnyOrderElementsOf(user.getAuthorities());
    }

    @Test
    void throwsWhenUserIsNotActive() {
        var user = buildUser(UserStatusEnum.INACTIVE);
        when(tokenService.validateToken("token")).thenReturn(user);

        assertThatThrownBy(() -> validateUserTokenCmd.validate("token"))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void propagatesExceptionFromTokenService() {
        when(tokenService.validateToken("bad-token")).thenThrow(new AppAuthException("Invalid user token."));

        assertThatThrownBy(() -> validateUserTokenCmd.validate("bad-token"))
            .isInstanceOf(AppAuthException.class);
    }
}
