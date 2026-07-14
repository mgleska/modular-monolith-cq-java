package com.mgleska.mmcqjava2.store.action.query;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JoinStoreByIdJpqlQryTest {

    private final JoinStoreByIdJpqlQry joinStoreByIdJpqlQry = new JoinStoreByIdJpqlQry();

    @Test
    void usesProvidedAlias() {
        var result = joinStoreByIdJpqlQry.joinById("o.storeId", "s");

        assertThat(result.alias).isEqualTo("s");
        assertThat(result.jpqlStatement).isEqualTo("com.mgleska.mmcqjava2.store.model.Store AS s ON o.storeId = s.id ");
    }

    @Test
    void defaultsAliasToSimpleClassNameWhenAliasIsNull() {
        var result = joinStoreByIdJpqlQry.joinById("o.storeId", null);

        assertThat(result.alias).isEqualTo("Store");
        assertThat(result.jpqlStatement).isEqualTo("com.mgleska.mmcqjava2.store.model.Store AS Store ON o.storeId = Store.id ");
    }

    @Test
    void defaultsAliasToSimpleClassNameWhenAliasIsEmpty() {
        var result = joinStoreByIdJpqlQry.joinById("o.storeId", "");

        assertThat(result.alias).isEqualTo("Store");
    }

    @Test
    void providesIdAndNameColumns() {
        var result = joinStoreByIdJpqlQry.joinById("o.storeId", "s");

        assertThat(result.providesColumns).isEqualTo(Map.of(
            "id", "int",
            "name", "String"
        ));
    }

    @Test
    void setsBuilderNameToOwnClassName() {
        var result = joinStoreByIdJpqlQry.joinById("o.storeId", "s");

        assertThat(result.builderName).isEqualTo(JoinStoreByIdJpqlQry.class.getName());
    }
}
