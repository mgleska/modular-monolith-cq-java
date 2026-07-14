package com.mgleska.mmcqjava2.user.action.command;

import com.mgleska.mmcqjava2.user.enums.RoleEnum;
import com.mgleska.mmcqjava2.user.model.User;
import com.mgleska.mmcqjava2.user.model.UserRepository;
import com.mgleska.mmcqjava2.user.support.UserTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminLoginCmdTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTokenService tokenService;

    @InjectMocks
    private AdminLoginCmd adminLoginCmd;

    @Test
    void returnsExistingTokenWhenUserAlreadyExists() {
        var existingUser = new User();
        existingUser.setEmail("user@example.com");
        existingUser.setToken("existing-token");
        when(userRepository.findByEmail("user@example.com")).thenReturn(existingUser);

        var result = adminLoginCmd.handle(new AdminLoginCmd.ParamDto("user@example.com", "User Name"));

        assertThat(result.token()).isEqualTo("existing-token");
        verify(userRepository, times(0)).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createsNewUserWithGeneratedTokenAndDefaultRoleWhenNotFound() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(tokenService.generateToken(0)).thenReturn("generated-token");

        var result = adminLoginCmd.handle(new AdminLoginCmd.ParamDto("new@example.com", "New User"));

        assertThat(result.token()).isEqualTo("generated-token");

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(2)).save(captor.capture());
        var savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getPassword()).isEmpty();
        assertThat(savedUser.getToken()).isEqualTo("generated-token");
        assertThat(savedUser.getRoles()).isEqualTo(Set.of(RoleEnum.ROLE_USER));
    }
}
