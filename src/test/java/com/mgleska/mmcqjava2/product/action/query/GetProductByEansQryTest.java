package com.mgleska.mmcqjava2.product.action.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByEansQryTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private GetProductByEansQry getProductByEansQry;

    @SuppressWarnings("unchecked")
    private final TypedQuery<GetProductByEansQry.ResultItemDto> typedQuery = mock(TypedQuery.class);

    @Test
    void returnsEmptyListWithoutQueryingWhenEansIsEmpty() {
        var result = getProductByEansQry.handle(Set.of());

        assertThat(result).isEmpty();
        verify(em, never()).createQuery(anyString(), eq(GetProductByEansQry.ResultItemDto.class));
    }

    @Test
    void returnsResultsFromQueryFilteredByEans() {
        when(em.createQuery(anyString(), eq(GetProductByEansQry.ResultItemDto.class))).thenReturn(typedQuery);
        var eans = Set.of("1234567890123", "9876543210987");
        when(typedQuery.setParameter("eans", eans)).thenReturn(typedQuery);
        var items = List.of(
            new GetProductByEansQry.ResultItemDto(1, "1234567890123"),
            new GetProductByEansQry.ResultItemDto(2, "9876543210987")
        );
        when(typedQuery.getResultList()).thenReturn(items);

        var result = getProductByEansQry.handle(eans);

        assertThat(result).isEqualTo(items);
        verify(typedQuery).setParameter("eans", eans);
    }
}
