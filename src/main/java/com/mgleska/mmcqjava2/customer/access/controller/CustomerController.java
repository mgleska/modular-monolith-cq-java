package com.mgleska.mmcqjava2.customer.access.controller;

import com.mgleska.mmcqjava2.customer.action.command.ChangeStoreCmd;
import com.mgleska.mmcqjava2.customer.action.command.LoginCmd;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Mobile API")
public class CustomerController {

    private final LoginCmd loginCmd;
    private final ChangeStoreCmd changeStoreCmd;

    public CustomerController(LoginCmd loginCmd,  ChangeStoreCmd changeStoreCmd) {
        this.loginCmd = loginCmd;
        this.changeStoreCmd = changeStoreCmd;
    }

    @PostMapping(value = "/api/customer/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    public LoginCmd.ResultDto login(@RequestBody @Valid LoginCmd.ParamDto dto) throws AppValidationException {
        return loginCmd.handle(dto);
    }

    @PostMapping(value = "/api/customer/change-store", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChangeStoreCmd.ResultDto changeStore(@RequestBody @Valid ChangeStoreCmd.ParamDto dto) throws AppValidationException {
        return changeStoreCmd.handle(dto);
    }
}
