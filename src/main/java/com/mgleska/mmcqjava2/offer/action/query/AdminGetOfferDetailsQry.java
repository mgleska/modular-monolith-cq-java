package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.product.action.query.GetProductDetailsQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AdminGetOfferDetailsQry {

    public record ResultDto(
        @NotNull  int id,
        @NotNull  int version,
        @NotNull  boolean visible,
        @NotBlank String productEan,
        @NotBlank String productName,
        @NotNull  int price,
                  Integer lowestPrice,
        @NotBlank String imageUrl,
                  Integer quantity
    ) {}

    private final OfferRepository offerRepository;
    private final GetProductDetailsQry getProductDetailsQry;

    public AdminGetOfferDetailsQry(OfferRepository offerRepository, GetProductDetailsQry getProductDetailsQry) {
        this.offerRepository = offerRepository;
        this.getProductDetailsQry = getProductDetailsQry;
    }

    public ResultDto handle(int id) {

        var offer = offerRepository.findById(id).orElse(null);
        if (offer == null) {
            throw new AppValidationException("offerId", "Offer not found");
        }

        GetProductDetailsQry.ResultDto product = null;
        if (offer.getProductId() != null) {
            product = getProductDetailsQry.handle(offer.getProductId(), offer.getStoreId());
        }

        return new ResultDto(
            offer.getId(),
            offer.getVersion(),
            offer.isVisible(),
            offer.getProductEan(),
            offer.getProductName() != null ? offer.getProductName() : (product != null ? product.name() : ""),
            offer.getPrice(),
            offer.getLowestPrice(),
            product != null ? (product.imageUrl() != null ? product.imageUrl() : "" ) : "",
            product != null ? product.quantity() : null
        );
    }
}
