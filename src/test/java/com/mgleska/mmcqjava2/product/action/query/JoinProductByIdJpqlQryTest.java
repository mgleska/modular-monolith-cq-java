package com.mgleska.mmcqjava2.product.action.query;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JoinProductByIdJpqlQryTest {

    private final JoinProductByIdJpqlQry joinProductByIdJpqlQry = new JoinProductByIdJpqlQry();

    @Test
    void usesProvidedAlias() {
        var result = joinProductByIdJpqlQry.joinById("o.productId", "p");

        assertThat(result.alias).isEqualTo("p");
        assertThat(result.jpqlStatement).isEqualTo("com.mgleska.mmcqjava2.product.model.Product AS p ON o.productId = p.id ");
    }

    @Test
    void defaultsAliasToSimpleClassNameWhenAliasIsNull() {
        var result = joinProductByIdJpqlQry.joinById("o.productId", null);

        assertThat(result.alias).isEqualTo("Product");
        assertThat(result.jpqlStatement).isEqualTo("com.mgleska.mmcqjava2.product.model.Product AS Product ON o.productId = Product.id ");
    }

    @Test
    void defaultsAliasToSimpleClassNameWhenAliasIsEmpty() {
        var result = joinProductByIdJpqlQry.joinById("o.productId", "");

        assertThat(result.alias).isEqualTo("Product");
    }

    @Test
    void providesIdEanNameAndImageUrlColumns() {
        var result = joinProductByIdJpqlQry.joinById("o.productId", "p");

        assertThat(result.providesColumns).isEqualTo(Map.of(
            "id", "int",
            "ean", "String",
            "name", "String",
            "imageUrl", "String"
        ));
    }

    @Test
    void setsBuilderNameToOwnClassName() {
        var result = joinProductByIdJpqlQry.joinById("o.productId", "p");

        assertThat(result.builderName).isEqualTo(JoinProductByIdJpqlQry.class.getName());
    }
}
