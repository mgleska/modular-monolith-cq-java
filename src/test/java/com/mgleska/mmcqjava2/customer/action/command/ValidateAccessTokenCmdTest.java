package com.mgleska.mmcqjava2.customer.action.command;

import com.auth0.jwt.interfaces.Claim;
import com.mgleska.mmcqjava2.customer.support.TokenService;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateAccessTokenCmdTest {

    private static final String JWT = "some.jwt.token";

    @Mock
    private TokenService tokenService;

    @Mock
    private Claim uidClaim;

    @Mock
    private Claim stidClaim;

    @InjectMocks
    private ValidateAccessTokenCmd validateAccessTokenCmd;

    @Test
    void returnsTokenWithCustomerIdAndStoreId() {
        when(uidClaim.asInt()).thenReturn(12);
        when(stidClaim.asInt()).thenReturn(2);
        when(tokenService.decodeAccessToken(JWT)).thenReturn(Map.of("uid", uidClaim, "stid", stidClaim));

        var result = validateAccessTokenCmd.validate(JWT);

        assertThat(result.getCustomerId()).isEqualTo(12);
        assertThat(result.getStoreId()).isEqualTo(2);
    }

    @Test
    void defaultsStoreIdToZeroWhenStidMissing() {
        when(uidClaim.asInt()).thenReturn(12);
        when(tokenService.decodeAccessToken(JWT)).thenReturn(Map.of("uid", uidClaim));

        var result = validateAccessTokenCmd.validate(JWT);

        assertThat(result.getCustomerId()).isEqualTo(12);
        assertThat(result.getStoreId()).isZero();
    }

    @Test
    void throwsWhenUidMissing() {
        when(tokenService.decodeAccessToken(JWT)).thenReturn(Map.of("stid", stidClaim));

        assertThatThrownBy(() -> validateAccessTokenCmd.validate(JWT))
            .isInstanceOf(AppNeverException.class);
    }
}
