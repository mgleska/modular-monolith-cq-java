package com.mgleska.mmcqjava2.store.model;

import org.springframework.data.repository.CrudRepository;

public interface StoreRepository extends CrudRepository<Store, Integer> {

    Store findByExternalId(String externalId);

}
