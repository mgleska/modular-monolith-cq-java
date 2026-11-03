package com.mgleska.mmcqjava2.user.access.controller;

import com.mgleska.mmcqjava2.mailer.action.enums.DeliveryStatusEnum;
import com.mgleska.mmcqjava2.mailer.action.query.GetDeliveryStatusForIds;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.user.action.command.MailDemoCmd;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@Tag(name = "Admin")
public class DemoController {

    private final MailDemoCmd mailDemoCmd;
    private final GetDeliveryStatusForIds getDeliveryStatusForIds;

    public DemoController(MailDemoCmd mailDemoCmd, GetDeliveryStatusForIds getDeliveryStatusForIds) {
        this.mailDemoCmd = mailDemoCmd;
        this.getDeliveryStatusForIds = getDeliveryStatusForIds;
    }

    @PostMapping(value = "/api/admin/user/trigger-mail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void triggerMail(@RequestBody @Valid MailDemoCmd.ParamDto dto) throws AppValidationException {
        mailDemoCmd.handle(dto);
    }

    @GetMapping(value = "/api/admin/user/mail-status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long, DeliveryStatusEnum> mailDeliveryStatus(@RequestParam ArrayList<Long> ids) throws AppValidationException {
        return getDeliveryStatusForIds.handle(ids);
    }
}
