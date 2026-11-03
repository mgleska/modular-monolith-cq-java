package com.mgleska.mmcqjava2.user.action.command;

import com.mgleska.mmcqjava2.mailer.action.command.SendMail;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailDemoCmd {

    public record ParamDto(
        @NotNull String email,
        @NotNull String message
    ) {}

    private final SendMail sendMail;

    public MailDemoCmd(SendMail sendMail) {
        this.sendMail = sendMail;
    }

    public void handle(ParamDto dto) throws AppValidationException {
        var result = sendMail.handle(new SendMail.ParamDto("UserMailDemo", 1, dto.email(), dto.message()));
        log.info("Mail referenceId: {}", result.referenceId());
    }
}
