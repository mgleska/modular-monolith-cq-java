package com.mgleska.mmcqjava2.store.action.query;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetStoreListQry {

    public record ListItemDto(
        @NotNull  int id,
        @NotBlank String externalId,
        @NotBlank String name
    ) {}

    private final EntityManager entityManager;

    public GetStoreListQry(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ListItemDto> handle() {
        var query = entityManager.createQuery(
            "SELECT s.id, s.externalId, s.name FROM Store s ORDER BY s.name ASC",
            ListItemDto.class
        );

        return query.getResultList();
    }
}
