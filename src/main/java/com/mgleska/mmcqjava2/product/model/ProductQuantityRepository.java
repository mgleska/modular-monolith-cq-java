package com.mgleska.mmcqjava2.product.model;

import org.springframework.data.repository.CrudRepository;

public interface ProductQuantityRepository extends CrudRepository<ProductQuantity, Integer> {

    ProductQuantity findOneByProductIdAndStoreId(int productId, int storeId);
}
