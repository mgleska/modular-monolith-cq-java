package com.mgleska.mmcqjava2.user.action.command;

import com.mgleska.mmcqjava2.user.enums.RoleEnum;
import com.mgleska.mmcqjava2.user.model.User;
import com.mgleska.mmcqjava2.user.model.UserRepository;
import com.mgleska.mmcqjava2.user.support.UserTokenService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminLoginCmd {

    public record ParamDto(
        @NotNull String email,
                 String name
    ) {}

    public record ResultDto(
        @NotBlank String token
    ) {}

    private final UserRepository userRepository;
    private final UserTokenService tokenService;

    public AdminLoginCmd(UserRepository userRepository, UserTokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public ResultDto handle(ParamDto dto) {

        var user = this.userRepository.findByEmail(dto.email);

        if (user != null) {
            return new ResultDto(user.getToken());
        }

        user = new User();
        user.setEmail(dto.email);
        user.setName(dto.name);
        user.setPassword("");
        userRepository.save(user);

        user.setToken(tokenService.generateToken(user.getId()));
        user.setRoles(Set.of(RoleEnum.ROLE_USER));
        userRepository.save(user);

        return new ResultDto(user.getToken());
    }
}
