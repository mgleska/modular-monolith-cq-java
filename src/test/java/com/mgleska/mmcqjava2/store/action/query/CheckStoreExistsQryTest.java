package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.store.model.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckStoreExistsQryTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CheckStoreExistsQry checkStoreExistsQry;

    @Test
    void returnsTrueWhenStoreExists() {
        when(storeRepository.existsById(5)).thenReturn(true);

        assertThat(checkStoreExistsQry.check(5)).isTrue();
    }

    @Test
    void returnsFalseWhenStoreDoesNotExist() {
        when(storeRepository.existsById(5)).thenReturn(false);

        assertThat(checkStoreExistsQry.check(5)).isFalse();
    }
}
