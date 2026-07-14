package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.offer.model.Offer;
import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.product.action.query.GetProductDetailsQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AdminGetOfferDetailsQryTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private GetProductDetailsQry getProductDetailsQry;

    @InjectMocks
    private AdminGetOfferDetailsQry adminGetOfferDetailsQry;

    private Offer buildOffer(Integer productId) {
        var offer = new Offer();
        offer.setVersion(1);
        offer.setVisible(true);
        offer.setStoreId(7);
        offer.setProductEan("1234567890123");
        offer.setPrice(500);
        offer.setLowestPrice(400);
        offer.setProductId(productId);
        return offer;
    }

    @Test
    void throwsWhenOfferNotFound() {
        when(offerRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminGetOfferDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("offerId"));

        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void returnsDetailsWithoutFetchingProductWhenProductIdIsNull() {
        var offer = buildOffer(null);
        offer.setProductName("Offer Name");
        when(offerRepository.findById(5)).thenReturn(Optional.of(offer));

        var result = adminGetOfferDetailsQry.handle(5);

        assertThat(result.productName()).isEqualTo("Offer Name");
        assertThat(result.imageUrl()).isEmpty();
        assertThat(result.quantity()).isNull();
        verify(getProductDetailsQry, never()).handle(anyInt(), anyInt());
    }

    @Test
    void mergesProductDetailsWhenProductIdIsPresentAndOfferHasNoProductName() {
        var offer = buildOffer(9);
        offer.setProductName(null);
        when(offerRepository.findById(5)).thenReturn(Optional.of(offer));
        when(getProductDetailsQry.handle(9, 7)).thenReturn(
            new GetProductDetailsQry.ResultDto(9, "1234567890123", "Product Name", "http://image", 3)
        );

        var result = adminGetOfferDetailsQry.handle(5);

        assertThat(result.id()).isEqualTo(offer.getId());
        assertThat(result.version()).isEqualTo(1);
        assertThat(result.visible()).isTrue();
        assertThat(result.productEan()).isEqualTo("1234567890123");
        assertThat(result.productName()).isEqualTo("Product Name");
        assertThat(result.price()).isEqualTo(500);
        assertThat(result.lowestPrice()).isEqualTo(400);
        assertThat(result.imageUrl()).isEqualTo("http://image");
        assertThat(result.quantity()).isEqualTo(3);
    }

    @Test
    void prefersOfferProductNameOverFetchedProductName() {
        var offer = buildOffer(9);
        offer.setProductName("Offer Name");
        when(offerRepository.findById(5)).thenReturn(Optional.of(offer));
        when(getProductDetailsQry.handle(9, 7)).thenReturn(
            new GetProductDetailsQry.ResultDto(9, "1234567890123", "Product Name", null, null)
        );

        var result = adminGetOfferDetailsQry.handle(5);

        assertThat(result.productName()).isEqualTo("Offer Name");
        assertThat(result.imageUrl()).isEmpty();
        assertThat(result.quantity()).isNull();
    }
}
