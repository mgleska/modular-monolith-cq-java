package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.customer.action.query.GetCurrentCustomerStoreIdQry;
import com.mgleska.mmcqjava2.product.action.query.JoinProductByIdJpqlQry;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@SuppressWarnings("SqlSourceToSinkFlow")
@Service
public class GetOfferListQry {

    public record ParamDto(
        @Positive Integer page
    ) {}

    @NullMarked
    public record ResultDto(
        @NotNull List<ResultItemDto> items,
        @NotNull int page,
        @NotNull int perPage
    ) {}

    @NullMarked
    public record ResultItemDto(
        @NotNull  int id,
        @NotBlank String productEan,
        @NotBlank String productName,
        @NotNull  int price,
        @Nullable Integer lowestPrice
    ) {}

    private static final int PER_PAGE = 5;

    private final GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry;
    private final EntityManager em;
    private final JoinProductByIdJpqlQry joinProductByIdJpqlQry;

    public GetOfferListQry(GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry, EntityManager em, JoinProductByIdJpqlQry joinProductByIdJpqlQry) {
        this.getCurrentCustomerStoreIdQry = getCurrentCustomerStoreIdQry;
        this.em = em;
        this.joinProductByIdJpqlQry = joinProductByIdJpqlQry;
    }

    public ResultDto handle(ParamDto dto) {
        var storeId = getCurrentCustomerStoreIdQry.handle();

        var page = Math.max(dto.page == null ? 1 : dto.page, 1);

        var joinProduct = joinProductByIdJpqlQry.joinById("o.productId", "p");
        joinProduct.confirmRequiredColumns(Map.of("name", "String"));

        var query = em.createQuery(
        "SELECT o.id, o.productEan, COALESCE(o.productName, " + joinProduct.alias + ".name), o.price, o.lowestPrice " +
            "FROM Offer o " +
            "JOIN " + joinProduct.jpqlStatement +
            "WHERE o.storeId = :storeId AND o.visible = true", ResultItemDto.class
            )
            .setParameter("storeId", storeId)
            .setFirstResult((page - 1) * PER_PAGE)
            .setMaxResults(PER_PAGE);

        return new ResultDto(query.getResultList(), page, PER_PAGE);
    }
}
