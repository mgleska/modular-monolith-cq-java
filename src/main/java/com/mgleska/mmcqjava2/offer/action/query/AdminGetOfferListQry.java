package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.product.action.query.JoinProductByIdJpqlQry;
import com.mgleska.mmcqjava2.store.action.query.JoinStoreByIdJpqlQry;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminGetOfferListQry {

    public record ParamDto(
        String search,
        Integer storeId,
        Integer page,
        Integer perPage
    ) {}

    public record ResultDto(
        @NotNull List<ResultItemDto> items,
        @NotNull int page,
        @NotNull int perPage
    ) {}

    public record ResultItemDto(
        @NotNull  int id,
        @NotBlank String storeName,
        @NotNull  boolean visible,
        @NotBlank String productEan,
        @NotBlank String productName,
        @NotNull  int price
    ) {}

    private static final int PER_PAGE = 10;

    private final JoinStoreByIdJpqlQry joinStoreByIdJpqlQry;
    private final JoinProductByIdJpqlQry joinProductByIdJpqlQry;
    private final EntityManager em;

    public AdminGetOfferListQry(JoinStoreByIdJpqlQry joinStoreByIdJpqlQry,  JoinProductByIdJpqlQry joinProductByIdJpqlQry, EntityManager em) {
        this.joinStoreByIdJpqlQry = joinStoreByIdJpqlQry;
        this.joinProductByIdJpqlQry = joinProductByIdJpqlQry;
        this.em = em;
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public ResultDto handle(ParamDto dto) {
        var page = Math.max(dto.page == null ? 1 : dto.page, 1);
        var perPage = Math.max(dto.perPage == null ? PER_PAGE : dto.perPage, 1);

        var joinStore = joinStoreByIdJpqlQry.joinById("o.storeId", null);
        joinStore.confirmRequiredColumns(Map.of("id", "int", "name", "String"));

        var joinProduct = joinProductByIdJpqlQry.joinById("o.productId", null);
        joinProduct.confirmRequiredColumns(Map.of("id", "int", "name", "String"));

        var where = new ArrayList<String>();
        var params = new HashMap<String, Object>();

        if (dto.storeId != null) {
            where.add("o.storeId = :storeId");
            params.put("storeId", dto.storeId);
        }

        if (dto.search != null && ! dto.search.isBlank()) {
            where.add("(o.productEan LIKE :search OR COALESCE(o.productName, " + joinProduct.alias + ".name) LIKE :search)");
            params.put("search", "%" + dto.search + "%");
        }

        var sql = "SELECT o.id, " + joinStore.alias + ".name AS storeName, " + "o.visible, o.productEan, " +
                         "COALESCE(o.productName, " + joinProduct.alias + ".name), o.price " +
            "FROM Offer o " +
            "JOIN " + joinStore.jpqlStatement +
            "LEFT JOIN " + joinProduct.jpqlStatement;
        if (! where.isEmpty()) {
            sql += "WHERE " + String.join(" AND " , where) + " ";
        }
        sql += "ORDER BY " + joinStore.alias + ".name, " + joinProduct.alias + ".name";

        var query = em.createQuery(sql, ResultItemDto.class);

        params.forEach(query::setParameter);

        var items = query
            .setFirstResult((page - 1) * perPage)
            .setMaxResults(perPage)
            .getResultList();

        return new ResultDto(items, page, perPage);
    }
}
