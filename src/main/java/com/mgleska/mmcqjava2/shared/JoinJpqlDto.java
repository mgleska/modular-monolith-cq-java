package com.mgleska.mmcqjava2.shared;

import com.mgleska.mmcqjava2.shared.exception.AppNeverException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinJpqlDto {
    public final String jpqlStatement;
    public final Map<String, String> providesColumns;
    public final String alias;
    public final String builderName;

    private JoinJpqlDto(String jpqlStatement, Map<String, String> providesColumns, String alias, String builderName) {
        this.jpqlStatement = jpqlStatement;
        this.providesColumns = providesColumns;
        this.alias = alias;
        this.builderName = builderName;
    }

    public static JoinJpqlDto create(String entityClass, String alias, String condition, Map<String, String> providesColumns, String builderName) {
        if (alias.isEmpty()) {
            throw new AppNeverException("Entity alias cannot be empty");
        }

        return new JoinJpqlDto(
            entityClass + " AS " + alias + " ON " + condition + " ",
            providesColumns,
            alias,
            builderName
        );
    }

    public void confirmRequiredColumns(Map<String, String> required) {
        List<String> missing = new ArrayList<>();
        for (var item : required.entrySet()) {
            if (!providesColumns.containsKey(item.getKey()) || !item.getValue().equals(required.get(item.getKey()))) {
                missing.add(item.getKey());
            }
        }
        if (!missing.isEmpty()) {
            throw new AppNeverException("SQL column list supplied by builder " + builderName +
                " do not contain all required columns. Missing columns: " + String.join(", ", missing));
        }
    }
}
