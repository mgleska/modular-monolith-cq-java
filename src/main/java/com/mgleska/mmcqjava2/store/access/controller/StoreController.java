package com.mgleska.mmcqjava2.store.access.controller;

import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.action.query.GetStoreDetailsQry;
import com.mgleska.mmcqjava2.store.action.query.GetStoreListQry;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Mobile API")
public class StoreController {

    private final GetStoreDetailsQry getStoreDetailsQry;
    private final GetStoreListQry getStoreListQry;
    public StoreController(GetStoreDetailsQry getStoreDetailsQry,  GetStoreListQry getStoreListQry) {
        this.getStoreDetailsQry = getStoreDetailsQry;
        this.getStoreListQry = getStoreListQry;
    }

    @GetMapping(value = "/api/store/list", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<GetStoreListQry.ListItemDto> list() throws AppValidationException {
        return getStoreListQry.handle();
    }

    @GetMapping(value = "/api/store/{id:[0-9]+}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetStoreDetailsQry.ResultDto details(@PathVariable int id) throws AppValidationException {
        return getStoreDetailsQry.handle(id);
    }
}
