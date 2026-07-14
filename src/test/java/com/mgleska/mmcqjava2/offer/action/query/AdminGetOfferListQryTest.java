package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.product.action.query.JoinProductByIdJpqlQry;
import com.mgleska.mmcqjava2.shared.JoinJpqlDto;
import com.mgleska.mmcqjava2.shared.exception.AppNeverException;
import com.mgleska.mmcqjava2.store.action.query.JoinStoreByIdJpqlQry;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminGetOfferListQryTest {

    @Mock
    private EntityManager em;

    @SuppressWarnings("unchecked")
    private final TypedQuery<AdminGetOfferListQry.ResultItemDto> typedQuery = mock(TypedQuery.class);

    private AdminGetOfferListQry adminGetOfferListQry;

    @BeforeEach
    void setUp() {
        adminGetOfferListQry = new AdminGetOfferListQry(new JoinStoreByIdJpqlQry(), new JoinProductByIdJpqlQry(), em);
    }

    private void stubQuery(List<AdminGetOfferListQry.ResultItemDto> items) {
        when(em.createQuery(anyString(), eq(AdminGetOfferListQry.ResultItemDto.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(items);
    }

    private String capturedSql() {
        var captor = ArgumentCaptor.forClass(String.class);
        verify(em).createQuery(captor.capture(), eq(AdminGetOfferListQry.ResultItemDto.class));
        return captor.getValue();
    }

    @Test
    void defaultsPageAndPerPageWhenNotProvided() {
        stubQuery(List.of());

        var result = adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null));

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.perPage()).isEqualTo(10);
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(10);
        assertThat(capturedSql()).doesNotContain("WHERE");
    }

    @Test
    void clampsPageAndPerPageToAtLeastOne() {
        stubQuery(List.of());

        var result = adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, -5, -5));

        assertThat(result.page()).isEqualTo(1);
        assertThat(result.perPage()).isEqualTo(1);
        verify(typedQuery).setFirstResult(0);
        verify(typedQuery).setMaxResults(1);
    }

    @Test
    void computesOffsetFromPageAndPerPage() {
        stubQuery(List.of());

        var result = adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, 3, 20));

        assertThat(result.page()).isEqualTo(3);
        assertThat(result.perPage()).isEqualTo(20);
        verify(typedQuery).setFirstResult(40);
        verify(typedQuery).setMaxResults(20);
    }

    @Test
    void filtersByStoreIdWhenProvided() {
        stubQuery(List.of());

        adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, 4, null, null));

        assertThat(capturedSql()).contains("WHERE o.storeId = :storeId");
        verify(typedQuery).setParameter("storeId", 4);
    }

    @Test
    void filtersBySearchTermWithWildcardsWhenProvided() {
        stubQuery(List.of());

        adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto("milk", null, null, null));

        assertThat(capturedSql())
            .contains("WHERE (o.productEan LIKE :search OR COALESCE(o.productName, Product.name) LIKE :search)");
        verify(typedQuery).setParameter("search", "%milk%");
    }

    @Test
    void ignoresBlankSearchTerm() {
        stubQuery(List.of());

        adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto("   ", null, null, null));

        assertThat(capturedSql()).doesNotContain("WHERE");
        verify(typedQuery, never()).setParameter(eq("search"), any());
    }

    @Test
    void combinesStoreIdAndSearchFiltersWithAnd() {
        stubQuery(List.of());

        adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto("milk", 4, null, null));

        var sql = capturedSql();
        assertThat(sql).contains("WHERE o.storeId = :storeId AND (o.productEan LIKE :search OR COALESCE(o.productName, Product.name) LIKE :search)");
    }

    @Test
    void buildsSqlWithJoinAliasesAndOrdering() {
        stubQuery(List.of());

        adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null));

        var sql = capturedSql();
        assertThat(sql)
            .contains("SELECT o.id, Store.name AS storeName")
            .contains("FROM Offer o")
            .contains("JOIN com.mgleska.mmcqjava2.store.model.Store AS Store ON o.storeId = Store.id")
            .contains("LEFT JOIN com.mgleska.mmcqjava2.product.model.Product AS Product ON o.productId = Product.id")
            .contains("ORDER BY Store.name, Product.name");
    }

    @Test
    void returnsItemsFromQuery() {
        var items = List.of(new AdminGetOfferListQry.ResultItemDto(1, "Store", true, "1234567890123", "Name", 500));
        stubQuery(items);

        var result = adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null));

        assertThat(result.items()).isEqualTo(items);
    }

    @Test
    void throwsWhenStoreJoinDoesNotProvideRequiredColumns() {
        var incompleteStoreJoin = new JoinStoreByIdJpqlQry() {
            @Override
            public JoinJpqlDto joinById(String foreignSelector, String alias) {
                return JoinJpqlDto.create("Store", "Store", foreignSelector + " = Store.id", Map.of(), "custom");
            }
        };
        adminGetOfferListQry = new AdminGetOfferListQry(incompleteStoreJoin, new JoinProductByIdJpqlQry(), em);

        assertThatThrownBy(() -> adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null)))
            .isInstanceOf(AppNeverException.class);
    }

    @Test
    void throwsWhenProductJoinDoesNotProvideRequiredColumns() {
        var incompleteProductJoin = new JoinProductByIdJpqlQry() {
            @Override
            public JoinJpqlDto joinById(String foreignSelector, String alias) {
                return JoinJpqlDto.create("Product", "Product", foreignSelector + " = Product.id", Map.of(), "custom");
            }
        };
        adminGetOfferListQry = new AdminGetOfferListQry(new JoinStoreByIdJpqlQry(), incompleteProductJoin, em);

        assertThatThrownBy(() -> adminGetOfferListQry.handle(new AdminGetOfferListQry.ParamDto(null, null, null, null)))
            .isInstanceOf(AppNeverException.class);
    }
}
