package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.store.model.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class GetStoreByExternalIdQry {

    public record ResultDto(
        int id,
        String externalId,
        String name
    ) {
    }

    private final StoreRepository repository;

    public GetStoreByExternalIdQry(StoreRepository repository) {
        this.repository = repository;
    }

    public ResultDto handle(String externalId) {
        var store = repository.findByExternalId(externalId);
        if (store == null) {
            return null;
        }

        return new ResultDto(
            store.getId(),
            store.getExternalId(),
            store.getName()
        );
    }
}
