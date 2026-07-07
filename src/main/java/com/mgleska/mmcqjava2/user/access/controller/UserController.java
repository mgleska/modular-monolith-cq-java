package com.mgleska.mmcqjava2.user.access.controller;

import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.user.action.command.AdminLoginCmd;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Admin")
public class UserController {

    private final AdminLoginCmd loginCmd;

    public UserController(AdminLoginCmd loginCmd) {
        this.loginCmd = loginCmd;
    }

    @PostMapping(value = "/api/admin/user/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    public AdminLoginCmd.ResultDto login(@RequestBody @Valid AdminLoginCmd.ParamDto dto) throws AppValidationException {
        return loginCmd.handle(dto);
    }
}
