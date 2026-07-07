package com.mgleska.mmcqjava2.offer.access.controller;

import com.mgleska.mmcqjava2.offer.action.command.ChangeVisibilityCmd;
import com.mgleska.mmcqjava2.offer.action.query.*;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Admin")
public class OfferAdminController {

    private final AdminGetListFiltersQry adminGetListFiltersQry;
    private final AdminGetOfferDetailsQry adminGetOfferDetailsQry;
    private final AdminGetOfferListQry adminGetOfferListQry;
    private final ChangeVisibilityCmd changeVisibilityCmd;

    public OfferAdminController(AdminGetListFiltersQry adminGetListFiltersQry, AdminGetOfferDetailsQry adminGetOfferDetailsQry,
                                AdminGetOfferListQry adminGetOfferListQry, ChangeVisibilityCmd changeVisibilityCmd) {
        this.adminGetListFiltersQry = adminGetListFiltersQry;
        this.adminGetOfferDetailsQry = adminGetOfferDetailsQry;
        this.adminGetOfferListQry = adminGetOfferListQry;
        this.changeVisibilityCmd = changeVisibilityCmd;
    }

    @GetMapping(value = "/api/admin/offer/filters", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AdminGetListFiltersQry.ResultDto filters() {
        return adminGetListFiltersQry.handle();
    }

    @GetMapping(value = "/api/admin/offer/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AdminGetOfferListQry.ResultDto list(@Valid AdminGetOfferListQry.ParamDto queryParam) {
        return adminGetOfferListQry.handle(queryParam);
    }

    @GetMapping(value = "/api/admin/offer/{id:[0-9]+}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AdminGetOfferDetailsQry.ResultDto details(@PathVariable int id) {
        return adminGetOfferDetailsQry.handle(id);
    }

    @PostMapping(value = "/api/admin/offer/change-visibility", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeVisibility(@RequestBody @Valid ChangeVisibilityCmd.ParamDto dto) throws AppValidationException {
        changeVisibilityCmd.handle(dto);
    }
}
