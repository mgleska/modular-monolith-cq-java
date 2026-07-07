package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.customer.action.query.GetCurrentCustomerStoreIdQry;
import com.mgleska.mmcqjava2.offer.action.enums.QuantityLevelEnum;
import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.product.action.query.GetProductDetailsQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GetOfferDetailsQry {

    public record ResultDto(
        @NotNull  int id,
        @NotBlank String productEan,
        @NotBlank String productName,
        @NotNull  int price,
                  Integer lowestPrice,
        @NotNull  String imageUrl,
        @NotNull  QuantityLevelEnum quantityLevel
    ) {}

    private static final int QUANTITY_LEVEL = 5000;

    private final GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry;
    private final OfferRepository repository;
    private final GetProductDetailsQry getProductDetailsQry;

    public GetOfferDetailsQry(GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry, OfferRepository repository, GetProductDetailsQry getProductDetailsQry) {
        this.getCurrentCustomerStoreIdQry = getCurrentCustomerStoreIdQry;
        this.repository = repository;
        this.getProductDetailsQry = getProductDetailsQry;
    }

    public ResultDto handle(int id) {

        var storeId = getCurrentCustomerStoreIdQry.handle();

        var offer = repository.findById(id).orElse(null);
        if (offer == null) {
            throw new AppValidationException("offerId", "Offer not found");
        }

        if (offer.getStoreId() != storeId) {
            throw new AppValidationException("offerId", "Requested offer belongs to other store.");
        }
        if (! offer.isVisible()) {
            throw new AppValidationException("visible", "Requested offer is not visible.");
        }
        if (offer.getProductId() == null) {
            throw new AppValidationException("productId", "Requested offer is for unknown product.");
        }

        var product = getProductDetailsQry.handle(offer.getProductId(), storeId);
        var quantityLevel = QuantityLevelEnum.UNKNOWN;
        if (product.quantity() != null && product.quantity() >= QUANTITY_LEVEL) {
            quantityLevel = QuantityLevelEnum.AVAILABLE;
        }
        else if (product.quantity() != null && product.quantity() > 0) {
            quantityLevel = QuantityLevelEnum.AVAILABLE_LOW;
        }

        return new ResultDto(
            offer.getId(),
            offer.getProductEan(),
            offer.getProductName() != null ? offer.getProductName() : product.name(),
            offer.getPrice(),
            offer.getLowestPrice(),
            product.imageUrl() != null ? product.imageUrl() : "",
            quantityLevel
        );
    }
}
