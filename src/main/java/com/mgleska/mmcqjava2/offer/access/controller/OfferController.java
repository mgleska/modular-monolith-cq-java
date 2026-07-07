package com.mgleska.mmcqjava2.offer.access.controller;

import com.mgleska.mmcqjava2.offer.action.query.GetOfferDetailsQry;
import com.mgleska.mmcqjava2.offer.action.query.GetOfferListQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Mobile API")
public class OfferController {

    private final GetOfferListQry getOfferListQry;
    private final GetOfferDetailsQry getOfferDetailsQry;

    public OfferController(GetOfferListQry getOfferListQry, GetOfferDetailsQry getOfferDetailsQry) {
        this.getOfferListQry = getOfferListQry;
        this.getOfferDetailsQry = getOfferDetailsQry;
    }

    @GetMapping(value = "/api/offer/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetOfferListQry.ResultDto list(@Valid GetOfferListQry.ParamDto queryParam) {
        return getOfferListQry.handle(queryParam);
    }

    @GetMapping(value = "/api/offer/{id:[0-9]+}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetOfferDetailsQry.ResultDto details(@PathVariable int id) throws AppValidationException {
        return getOfferDetailsQry.handle(id);
    }
}
