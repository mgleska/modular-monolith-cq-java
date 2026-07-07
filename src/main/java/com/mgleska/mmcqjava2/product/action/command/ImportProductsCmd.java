package com.mgleska.mmcqjava2.product.action.command;

import com.mgleska.mmcqjava2.product.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImportProductsCmd {

    private final EntityManager em;

    public ImportProductsCmd(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void handle() {

        record ApiRow(
            String ean,
            String name,
            String image
        ) {}

        // Fake import from external API
        List<ApiRow> apiData = new ArrayList<>();
        apiData.add(new ApiRow("ean-1","Red square", "red-square.png"));
        apiData.add(new ApiRow("ean-2","Blue square", "blue-square.png"));
        apiData.add(new ApiRow("ean-3","Green square", "green-square.png"));
        apiData.add(new ApiRow("ean-4","Red triangle", "red-triangle.png"));
        apiData.add(new ApiRow("ean-5","Blue triangle", "blue-triangle.png"));
        apiData.add(new ApiRow("ean-6","Green triangle", "green-triangle.png"));
        apiData.add(new ApiRow("ean-7","Red circle", "red-circle.png"));
        apiData.add(new ApiRow("ean-8","Blue circle", "blue-circle.png"));
        apiData.add(new ApiRow("ean-9","Green circle", "green-circle.png"));

        var eans = apiData
            .stream()
            .map(item -> item.ean)
            .toList();

        em.createQuery("DELETE FROM Product WHERE ean NOT IN (:eans)")
            .setParameter("eans", eans)
            .executeUpdate();

        var dbAll = em.createQuery("SELECT p FROM Product p", Product.class)
            .getResultStream()
            .collect(Collectors.toMap(Product::getEan, p -> p));

        for (var item : apiData) {
            var product = dbAll.get(item.ean);

            if (product == null) {
                var newProduct = new Product();
                newProduct.setEan(item.ean);
                newProduct.setName(item.name);
                newProduct.setImageUrl(item.image);
                em.persist(newProduct);
                continue;
            }

            if (! (
                item.ean.equals(product.getEan())
                && item.name.equals(product.getName())
                && Objects.equals(item.image, product.getImageUrl())
                )
            ) {
                product.setEan(item.ean);
                product.setName(item.name);
                product.setImageUrl(item.image);
                em.persist(product);
            }
        }

        em.flush();
    }
}
