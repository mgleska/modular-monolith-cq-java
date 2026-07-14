package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.customer.action.query.GetCurrentCustomerStoreIdQry;
import com.mgleska.mmcqjava2.offer.action.enums.QuantityLevelEnum;
import com.mgleska.mmcqjava2.offer.model.Offer;
import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.product.action.query.GetProductDetailsQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOfferDetailsQryTest {

    @Mock
    private GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry;

    @Mock
    private OfferRepository repository;

    @Mock
    private GetProductDetailsQry getProductDetailsQry;

    @InjectMocks
    private GetOfferDetailsQry getOfferDetailsQry;

    private Offer buildOffer(int storeId, boolean visible, Integer productId) {
        var offer = new Offer();
        offer.setStoreId(storeId);
        offer.setVisible(visible);
        offer.setProductId(productId);
        offer.setProductEan("1234567890123");
        offer.setPrice(500);
        offer.setLowestPrice(400);
        return offer;
    }

    @Test
    void throwsWhenOfferNotFound() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getOfferDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("offerId"));

        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void throwsWhenOfferBelongsToOtherStore() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(buildOffer(8, true, 9)));

        assertThatThrownBy(() -> getOfferDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("offerId"));

        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void throwsWhenOfferNotVisible() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(buildOffer(7, false, 9)));

        assertThatThrownBy(() -> getOfferDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("visible"));

        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void throwsWhenProductIdIsNull() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(buildOffer(7, true, null)));

        assertThatThrownBy(() -> getOfferDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("productId"));

        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void prefersOfferProductNameOverFetchedProductName() {
        var offer = buildOffer(7, true, 9);
        offer.setProductName("Offer Name");
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(offer));
        when(getProductDetailsQry.handle(9, 7)).thenReturn(
            new GetProductDetailsQry.ResultDto(9, "1234567890123", "Product Name", null, null)
        );

        var result = getOfferDetailsQry.handle(5);

        assertThat(result.productName()).isEqualTo("Offer Name");
        assertThat(result.imageUrl()).isEmpty();
        assertThat(result.quantityLevel()).isEqualTo(QuantityLevelEnum.UNKNOWN);
    }

    @Test
    void fallsBackToFetchedProductNameWhenOfferHasNone() {
        var offer = buildOffer(7, true, 9);
        offer.setProductName(null);
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(offer));
        when(getProductDetailsQry.handle(9, 7)).thenReturn(
            new GetProductDetailsQry.ResultDto(9, "1234567890123", "Product Name", "http://image", 10)
        );

        var result = getOfferDetailsQry.handle(5);

        assertThat(result.id()).isEqualTo(offer.getId());
        assertThat(result.productEan()).isEqualTo("1234567890123");
        assertThat(result.productName()).isEqualTo("Product Name");
        assertThat(result.price()).isEqualTo(500);
        assertThat(result.lowestPrice()).isEqualTo(400);
        assertThat(result.imageUrl()).isEqualTo("http://image");
    }

    @ParameterizedTest
    @CsvSource({
        ", UNKNOWN",
        "0, UNKNOWN",
        "1, AVAILABLE_LOW",
        "4999, AVAILABLE_LOW",
        "5000, AVAILABLE",
        "10000, AVAILABLE"
    })
    void mapsQuantityToQuantityLevel(Integer quantity, QuantityLevelEnum expectedLevel) {
        var offer = buildOffer(7, true, 9);
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        when(repository.findById(5)).thenReturn(Optional.of(offer));
        when(getProductDetailsQry.handle(9, 7)).thenReturn(
            new GetProductDetailsQry.ResultDto(9, "1234567890123", "Product Name", null, quantity)
        );

        var result = getOfferDetailsQry.handle(5);

        assertThat(result.quantityLevel()).isEqualTo(expectedLevel);
    }
}
