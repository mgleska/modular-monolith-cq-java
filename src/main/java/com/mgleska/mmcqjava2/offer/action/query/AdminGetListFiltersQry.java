package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.store.action.query.GetStoreListQry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminGetListFiltersQry {

    public record ResultDto(
        @NotNull List<ResultItemDto> stores
    ) {}

    public record ResultItemDto(
        @NotNull  int storeId,
        @NotBlank String storeName
    ) {}

    private final GetStoreListQry getStoreListQry;

    public AdminGetListFiltersQry(GetStoreListQry getStoreListQry) {
        this.getStoreListQry = getStoreListQry;
    }

    public ResultDto handle() {
        return new ResultDto(
            getStoreListQry.handle()
                .stream()
                .map(item -> new ResultItemDto(item.id(), item.name()))
                .toList()
        );
    }
}
