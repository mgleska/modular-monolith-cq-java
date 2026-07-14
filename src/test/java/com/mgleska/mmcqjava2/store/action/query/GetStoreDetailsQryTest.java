package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.model.Store;
import com.mgleska.mmcqjava2.store.model.StoreRepository;
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
class GetStoreDetailsQryTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private GetStoreDetailsQry getStoreDetailsQry;

    @Test
    void returnsStoreDetailsWhenFound() {
        var store = new Store("ext-5", "Store Five", "Main Street 5");
        when(storeRepository.findById(5)).thenReturn(Optional.of(store));

        var result = getStoreDetailsQry.handle(5);

        assertThat(result.id()).isEqualTo(5);
        assertThat(result.externalId()).isEqualTo("ext-5");
        assertThat(result.name()).isEqualTo("Store Five");
        assertThat(result.address()).isEqualTo("Main Street 5");
    }

    @Test
    void throwsWhenStoreNotFound() {
        when(storeRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getStoreDetailsQry.handle(5))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("id"));
    }
}
