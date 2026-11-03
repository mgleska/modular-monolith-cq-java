package com.mgleska.mmcqjava2.mailer.model;

import com.mgleska.mmcqjava2.mailer.support.DispatchStatusEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DispatchRepository extends CrudRepository<Dispatch, Long> {

    record IdStatusDto(long id, DispatchStatusEnum status) {}

    @Query("SELECT t.id, t.status FROM Dispatch t WHERE t.id IN (:ids)")
    List<IdStatusDto> findByIds(@Param("ids") List<Long> ids);
}
