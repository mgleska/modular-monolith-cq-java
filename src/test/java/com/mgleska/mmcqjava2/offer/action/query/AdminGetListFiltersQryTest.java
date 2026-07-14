package com.mgleska.mmcqjava2.offer.action.query;

import com.mgleska.mmcqjava2.store.action.query.GetStoreListQry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminGetListFiltersQryTest {

    @Mock
    private GetStoreListQry getStoreListQry;

    @InjectMocks
    private AdminGetListFiltersQry adminGetListFiltersQry;

    @Test
    void mapsStoresToResultItems() {
        when(getStoreListQry.handle()).thenReturn(List.of(
            new GetStoreListQry.ListItemDto(1, "ext-1", "Store One"),
            new GetStoreListQry.ListItemDto(2, "ext-2", "Store Two")
        ));

        var result = adminGetListFiltersQry.handle();

        assertThat(result.stores()).containsExactly(
            new AdminGetListFiltersQry.ResultItemDto(1, "Store One"),
            new AdminGetListFiltersQry.ResultItemDto(2, "Store Two")
        );
    }

    @Test
    void returnsEmptyListWhenNoStores() {
        when(getStoreListQry.handle()).thenReturn(List.of());

        var result = adminGetListFiltersQry.handle();

        assertThat(result.stores()).isEmpty();
    }
}
