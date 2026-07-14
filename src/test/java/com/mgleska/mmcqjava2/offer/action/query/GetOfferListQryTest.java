package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.customer.action.query.GetCurrentCustomerStoreIdQry;
import com.mgleska.mmcqjava2.product.action.query.JoinProductByIdJpqlQry;
import com.mgleska.mmcqjava2.shared.JoinJpqlDto;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOfferListQryTest {

    @Mock
    private GetCurrentCustomerStoreIdQry getCurrentCustomerStoreIdQry;

    @Mock
    private EntityManager em;

    @SuppressWarnings("unchecked")
    private final TypedQuery<GetOfferListQry.ResultItemDto> typedQuery = mock(TypedQuery.class);

    private GetOfferListQry getOfferListQry;

    @BeforeEach
    void setUp() {
        getOfferListQry = new GetOfferListQry(getCurrentCustomerStoreIdQry, em, new JoinProductByIdJpqlQry());
    }

    private void stubQuery(List<GetOfferListQry.ResultItemDto> items) {
        when(em.createQuery(anyString(), eq(GetOfferListQry.ResultItemDto.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(items);
    }

    @Test
    void defaultsToFirstPageWhenPageIsNull() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        stubQuery(List.of());

        var result = getOfferListQry.handle(new GetOfferListQry.ParamDto(null));

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.perPage()).isEqualTo(5);
        assertThat(result.items()).isEmpty();
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(5);
        verify(typedQuery).setParameter("storeId", 7);
    }

    @Test
    void usesRequestedPageForOffsetCalculation() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        stubQuery(List.of());

        var result = getOfferListQry.handle(new GetOfferListQry.ParamDto(3));

        assertThat(result.page()).isEqualTo(3);
        verify(typedQuery).setFirstResult(10);
        verify(typedQuery).setMaxResults(5);
    }

    @Test
    void returnsItemsFromQuery() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        var items = List.of(new GetOfferListQry.ResultItemDto(1, "1234567890123", "Name", 500, 400));
        stubQuery(items);

        var result = getOfferListQry.handle(new GetOfferListQry.ParamDto(1));

        assertThat(result.items()).isEqualTo(items);
    }

    @Test
    void buildsJpqlUsingJoinAliasAndFiltersOnStoreAndVisibility() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        stubQuery(List.of());

        getOfferListQry.handle(new GetOfferListQry.ParamDto(1));

        var jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(jpqlCaptor.capture(), eq(GetOfferListQry.ResultItemDto.class));
        assertThat(jpqlCaptor.getValue())
            .contains("FROM Offer o")
            .contains("o.storeId = :storeId")
            .contains("o.visible = true")
            .contains("COALESCE(o.productName, p.name)");
    }

    @Test
    void throwsWhenJoinDoesNotProvideRequiredColumns() {
        when(getCurrentCustomerStoreIdQry.handle()).thenReturn(7);
        var incompleteJoin = new JoinProductByIdJpqlQry() {
            @Override
            public JoinJpqlDto joinById(String foreignSelector, String alias) {
                return JoinJpqlDto.create("Product", "p", foreignSelector + " = p.id", Map.of(), "custom");
            }
        };
        getOfferListQry = new GetOfferListQry(getCurrentCustomerStoreIdQry, em, incompleteJoin);

        assertThatThrownBy(() -> getOfferListQry.handle(new GetOfferListQry.ParamDto(1)))
            .isInstanceOf(AppNeverException.class);
    }
}
