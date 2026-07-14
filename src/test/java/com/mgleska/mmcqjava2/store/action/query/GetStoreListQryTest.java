package com.mgleska.mmcqjava2.store.action.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStoreListQryTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GetStoreListQry getStoreListQry;

    @SuppressWarnings("unchecked")
    private final TypedQuery<GetStoreListQry.ListItemDto> typedQuery = mock(TypedQuery.class);

    @Test
    void returnsItemsOrderedByNameFromQuery() {
        when(entityManager.createQuery(
            "SELECT s.id, s.externalId, s.name FROM Store s ORDER BY s.name ASC",
            GetStoreListQry.ListItemDto.class
        )).thenReturn(typedQuery);
        var items = List.of(
            new GetStoreListQry.ListItemDto(1, "ext-1", "Store One"),
            new GetStoreListQry.ListItemDto(2, "ext-2", "Store Two")
        );
        when(typedQuery.getResultList()).thenReturn(items);

        var result = getStoreListQry.handle();

        assertThat(result).isEqualTo(items);
    }

    @Test
    void returnsEmptyListWhenNoStores() {
        when(entityManager.createQuery("SELECT s.id, s.externalId, s.name FROM Store s ORDER BY s.name ASC",
            GetStoreListQry.ListItemDto.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        assertThat(getStoreListQry.handle()).isEmpty();
    }
}
