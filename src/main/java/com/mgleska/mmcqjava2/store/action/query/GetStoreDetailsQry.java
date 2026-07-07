package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.model.StoreRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GetStoreDetailsQry {

    public record ResultDto(
        @NotNull  int id,
        @NotBlank String externalId,
        @NotBlank String name,
        @NotBlank String address
    ) {}

    private final StoreRepository storeRepository;

    public GetStoreDetailsQry(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public ResultDto handle(int id) throws AppValidationException {
        var store = storeRepository.findById(id).orElse(null);
        if (store == null) {
            throw new AppValidationException("id", "Store not found");
        }

        return new ResultDto(
                id,
                store.getExternalId(),
                store.getName(),
                store.getAddress()
        );
    }
}
