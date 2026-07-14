package com.mgleska.mmcqjava2.product.action.query;

import com.mgleska.mmcqjava2.product.model.Product;
import com.mgleska.mmcqjava2.product.model.ProductQuantity;
import com.mgleska.mmcqjava2.product.model.ProductQuantityRepository;
import com.mgleska.mmcqjava2.product.model.ProductRepository;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductDetailsQryTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductQuantityRepository quantityRepository;

    @InjectMocks
    private GetProductDetailsQry getProductDetailsQry;

    private Product buildProduct() {
        var product = new Product();
        product.setEan("1234567890123");
        product.setName("Product Name");
        product.setImageUrl("http://image");
        return product;
    }

    @Test
    void throwsWhenProductNotFound() {
        when(productRepository.findById(9)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getProductDetailsQry.handle(9, 7))
            .isInstanceOf(AppNeverException.class);
    }

    @Test
    void returnsProductDetailsWithQuantityWhenQuantityRecordExists() {
        when(productRepository.findById(9)).thenReturn(Optional.of(buildProduct()));
        var productQuantity = new ProductQuantity();
        productQuantity.setQuantity(15);
        when(quantityRepository.findOneByProductIdAndStoreId(9, 7)).thenReturn(productQuantity);

        var result = getProductDetailsQry.handle(9, 7);

        assertThat(result.id()).isEqualTo(9);
        assertThat(result.ean()).isEqualTo("1234567890123");
        assertThat(result.name()).isEqualTo("Product Name");
        assertThat(result.imageUrl()).isEqualTo("http://image");
        assertThat(result.quantity()).isEqualTo(15);
    }

    @Test
    void returnsNullQuantityWhenNoQuantityRecordExists() {
        when(productRepository.findById(9)).thenReturn(Optional.of(buildProduct()));
        when(quantityRepository.findOneByProductIdAndStoreId(9, 7)).thenReturn(null);

        var result = getProductDetailsQry.handle(9, 7);

        assertThat(result.quantity()).isNull();
    }
}
