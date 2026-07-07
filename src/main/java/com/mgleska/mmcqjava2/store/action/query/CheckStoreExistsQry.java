package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.store.model.StoreRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckStoreExistsQry {

    private final StoreRepository storeRepository;

    public CheckStoreExistsQry(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public boolean check(int id) {
        return storeRepository.existsById(id);
    }
}
