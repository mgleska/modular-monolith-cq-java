package com.mgleska.mmcqjava2.store.action.query;

import com.mgleska.mmcqjava2.shared.JoinJpqlDto;
import com.mgleska.mmcqjava2.store.model.Store;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class JoinStoreByIdJpqlQry {

    public JoinJpqlDto joinById(String foreignSelector, String alias) {
        if (alias == null || alias.isEmpty()) {
            var tmp = foreignSelector.split("[.]");
            alias = tmp[tmp.length - 1];
        }

        var provides = new HashMap<String, String>();
        provides.put("id", "int");
        provides.put("name", "String");

        return JoinJpqlDto.create(
            Store.class.getName(),
            alias,
            foreignSelector + " = " + alias + ".id",
            provides,
            JoinStoreByIdJpqlQry.class.getName()
        );
    }
}
