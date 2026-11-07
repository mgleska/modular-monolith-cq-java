package com.mgleska.mmcqjava2.user.access.controller;

import com.mgleska.mmcqjava2.mailer.action.enums.DeliveryStatusEnum;
import com.mgleska.mmcqjava2.mailer.action.query.GetDeliveryStatusForIds;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.user.action.command.MailDemoCmd;
import com.mgleska.mmcqjava2.user.action.command.PushDemoCmd;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Admin")
public class DemoController {

    private final MailDemoCmd mailDemoCmd;
    private final GetDeliveryStatusForIds getDeliveryStatusForIds;
    private final PushDemoCmd pushDemoCmd;

    public DemoController(MailDemoCmd mailDemoCmd, GetDeliveryStatusForIds getDeliveryStatusForIds, PushDemoCmd pushDemoCmd) {
        this.mailDemoCmd = mailDemoCmd;
        this.getDeliveryStatusForIds = getDeliveryStatusForIds;
        this.pushDemoCmd = pushDemoCmd;
    }

    @PostMapping(value = "/api/admin/user/trigger-mail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void triggerMail(@RequestBody @Valid MailDemoCmd.ParamDto dto) throws AppValidationException {
        mailDemoCmd.handle(dto);
    }

    @GetMapping(value = "/api/admin/user/mail-status")
    public Map<Long, DeliveryStatusEnum> mailDeliveryStatus(@RequestParam List<Long> ids) throws AppValidationException {
        return getDeliveryStatusForIds.handle(ids);
    }

    @PostMapping(value = "/api/admin/user/trigger-push", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void triggerPush(@RequestBody @Valid PushDemoCmd.ParamDto dto) throws AppValidationException {
        pushDemoCmd.handle(dto);
    }

}
