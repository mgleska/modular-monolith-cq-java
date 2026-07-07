package com.mgleska.mmcqjava2.product.action.command;

import com.mgleska.mmcqjava2.product.model.Product;
import com.mgleska.mmcqjava2.product.model.ProductQuantity;
import com.mgleska.mmcqjava2.product.model.ProductRepository;
import com.mgleska.mmcqjava2.store.action.query.GetStoreListQry;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.StreamSupport;

@Service
public class ImportQuantityCmd {

    private final GetStoreListQry getStoreListQry;
    private final ProductRepository productRepository;
    private final EntityManager em;

    public ImportQuantityCmd(GetStoreListQry getStoreListQry, ProductRepository productRepository, EntityManager em) {
        this.getStoreListQry = getStoreListQry;
        this.productRepository = productRepository;
        this.em = em;
    }

    Random random = new Random();

    @Transactional
    public void handle() {

        // Fake import from external API

        var stores = getStoreListQry.handle();
        var ids = StreamSupport.stream(productRepository.findAll().spliterator(), false)
                    .map(Product::getId)
                    .toList();

        em.createNativeQuery("TRUNCATE " + ProductQuantity.TABLE).executeUpdate();
        em.clear();

        for (var store : stores) {
            for (var id : ids) {
                var quantity = random.nextInt(0, 21);
                if (quantity == 0) {
                    continue;
                }
                var entity = new ProductQuantity();
                entity.setStoreId(store.id());
                entity.setProductId(id);
                entity.setQuantity(quantity * 1000);
                em.persist(entity);
            }
        }

        em.flush();
    }
}
