package com.mgleska.mmcqjava2.product.action.query;

import com.mgleska.mmcqjava2.product.model.ProductQuantityRepository;
import com.mgleska.mmcqjava2.product.model.ProductRepository;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GetProductDetailsQry {

    @NullMarked
    public record ResultDto(
        int id,
        String ean,
        String name,
        @Nullable String imageUrl,
        @Nullable Integer quantity
    ) {}

    private final ProductRepository productRepository;
    private final ProductQuantityRepository quantityRepository;

    public GetProductDetailsQry(ProductRepository productRepository, ProductQuantityRepository productQuantityRepository) {
        this.productRepository = productRepository;
        this.quantityRepository = productQuantityRepository;
    }

    public ResultDto handle(int productId, int storeId) {

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new AppNeverException("Product not found; productId = " + productId);
        }

        var quantity = quantityRepository.findOneByProductIdAndStoreId(productId, storeId);

        return new ResultDto(
            productId,
            product.getEan(),
            product.getName(),
            product.getImageUrl(),
            quantity != null ? quantity.getQuantity() : null
        );
    }
}
