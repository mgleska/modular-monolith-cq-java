package com.mgleska.mmcqjava2.product.action.query;

import com.mgleska.mmcqjava2.product.model.Product;
import com.mgleska.mmcqjava2.shared.JoinJpqlDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class JoinProductByIdJpqlQry {

    public JoinJpqlDto joinById(String foreignSelector, String alias) {
        if (alias == null || alias.isEmpty()) {
            var tmp = Product.class.getName().split("[.]");
            alias = tmp[tmp.length - 1];
        }

        var provides = new HashMap<String, String>();
        provides.put("id", "int");
        provides.put("ean", "String");
        provides.put("name", "String");
        provides.put("imageUrl", "String");

        return JoinJpqlDto.create(
            Product.class.getName(),
            alias,
            foreignSelector + " = " + alias + ".id",
            provides,
            JoinProductByIdJpqlQry.class.getName()
        );
    }
}
