package com.mgleska.mmcqjava2.offer.model;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

public interface OfferRepository extends CrudRepository<Offer, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Offer findWithLockById(int id);
}
