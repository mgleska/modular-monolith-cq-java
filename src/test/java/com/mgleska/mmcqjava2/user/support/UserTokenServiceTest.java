package com.mgleska.mmcqjava2.user.support;

import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import com.mgleska.mmcqjava2.user.model.User;
import com.mgleska.mmcqjava2.user.model.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserTokenService userTokenService;

    @Test
    void generateTokenStartsWithUserIdPrefix() {
        var token = userTokenService.generateToken(7);

        assertThat(token).startsWith("7-");
    }

    @Test
    void generateTokenIsRandomOnEachCall() {
        var first = userTokenService.generateToken(7);
        var second = userTokenService.generateToken(7);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void validateTokenReturnsUserWhenTokenMatches() {
        var user = new User();
        user.setToken("5-secret");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        assertThat(userTokenService.validateToken("5-secret")).isEqualTo(user);
    }

    @Test
    void validateTokenThrowsWhenTokenHasNoSeparator() {
        assertThatThrownBy(() -> userTokenService.validateToken("notoken"))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void validateTokenThrowsWhenTokenHasTooManyParts() {
        assertThatThrownBy(() -> userTokenService.validateToken("5-secret-extra"))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void validateTokenThrowsWhenUserNotFound() {
        when(userRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userTokenService.validateToken("5-secret"))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void validateTokenThrowsWhenStoredTokenDoesNotMatch() {
        var user = new User();
        user.setToken("5-other-secret");
        when(userRepository.findById(5)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userTokenService.validateToken("5-secret"))
            .isInstanceOf(AppAuthException.class);
    }
}
