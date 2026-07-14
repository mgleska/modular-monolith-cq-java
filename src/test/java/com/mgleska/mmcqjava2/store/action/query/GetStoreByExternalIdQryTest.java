package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.store.model.Store;
import com.mgleska.mmcqjava2.store.model.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStoreByExternalIdQryTest {

    @Mock
    private StoreRepository repository;

    @InjectMocks
    private GetStoreByExternalIdQry getStoreByExternalIdQry;

    @Test
    void returnsStoreDetailsWhenFound() {
        var store = new Store("ext-5", "Store Five", "Main Street 5");
        when(repository.findByExternalId("ext-5")).thenReturn(store);

        var result = getStoreByExternalIdQry.handle("ext-5");

        assertThat(result.id()).isEqualTo(store.getId());
        assertThat(result.externalId()).isEqualTo("ext-5");
        assertThat(result.name()).isEqualTo("Store Five");
    }

    @Test
    void returnsNullWhenStoreNotFound() {
        when(repository.findByExternalId("missing")).thenReturn(null);

        assertThat(getStoreByExternalIdQry.handle("missing")).isNull();
    }
}
