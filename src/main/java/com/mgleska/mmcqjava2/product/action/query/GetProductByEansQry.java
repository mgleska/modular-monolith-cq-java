package com.mgleska.mmcqjava2.product.action.query;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class GetProductByEansQry {

    public record ResultItemDto(
        int id,
        String ean
    ) {}

    private final EntityManager em;

    public GetProductByEansQry(EntityManager em) {
        this.em = em;
    }

    public List<ResultItemDto> handle(Set<String> eans) {
        if (eans.isEmpty()) {
            return new ArrayList<>();
        }

        return em.createQuery("SELECT p.id, p.ean FROM Product p WHERE p.ean IN (:eans)", ResultItemDto.class)
            .setParameter("eans", eans)
            .getResultList();
    }
}
