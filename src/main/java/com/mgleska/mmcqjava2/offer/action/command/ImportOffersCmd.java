package com.mgleska.mmcqjava2.offer.action.command;

import com.mgleska.mmcqjava2.offer.model.Offer;
import com.mgleska.mmcqjava2.product.action.query.GetProductByEansQry;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import com.mgleska.mmcqjava2.store.action.query.GetStoreByExternalIdQry;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportOffersCmd {

    private final EntityManager em;
    private final GetStoreByExternalIdQry getStoreByExternalIdQry;
    private final GetProductByEansQry getProductByEansQry;

    public ImportOffersCmd(EntityManager em,  GetStoreByExternalIdQry getStoreByExternalIdQry,  GetProductByEansQry getProductByEansQry) {
        this.em = em;
        this.getStoreByExternalIdQry = getStoreByExternalIdQry;
        this.getProductByEansQry = getProductByEansQry;
    }

    Random random = new Random();

    @Transactional
    public void handle(String externalId) {

        var store = getStoreByExternalIdQry.handle(externalId);

        if (store == null) {
            throw new AppValidationException("storeExternalId", "Can not find store with externalID=" + externalId);
        }

        var apiData = generateFakeData();
        var eans = apiData
            .stream()
            .map(item -> item.productEan)
            .toList();

        em.createQuery("DELETE FROM Offer WHERE storeId = :storeId AND productEan NOT IN (:eans)")
            .setParameter("storeId", store.id())
            .setParameter("eans", eans)
            .executeUpdate();

        var dbAll = em.createQuery("SELECT o FROM Offer o WHERE storeId = :storeId", Offer.class)
            .setParameter("storeId", store.id())
            .getResultStream()
            .collect(Collectors.toMap(Offer::getProductEan, o -> o));

        Map<String, Offer> eanOfferMap = new HashMap<>();

        for (var item : apiData) {

            var offer = dbAll.get(item.productEan);

            if (offer == null) {
                var newOffer = new Offer();
                newOffer.setStoreId(store.id());
                newOffer.setExternalId(item.externalId);
                newOffer.setProductEan(item.productEan);
                newOffer.setProductName(item.productName);
                newOffer.setPrice(item.price);
                newOffer.setLowestPrice(item.lowestPrice);
                em.persist(newOffer);
                eanOfferMap.put(item.productEan, newOffer);
                continue;
            }

            if (notEqual(item,  offer)) {
                offer.setExternalId(item.externalId);
                offer.setProductEan(item.productEan);
                offer.setProductName(item.productName);
                offer.setPrice(item.price);
                offer.setLowestPrice(item.lowestPrice);
                em.persist(offer);
                eanOfferMap.put(item.productEan, offer);
            }
        }

        var products = getProductByEansQry.handle(eanOfferMap.keySet());
        for (var dto : products) {
            eanOfferMap.get(dto.ean()).setProductId(dto.id());
        }

        em.flush();
    }

    static class ApiRow {
        String productEan;
        String productName;
        int price;
        Integer lowestPrice;
        String externalId;

        public ApiRow(String productEan, String productName, int price) {
            this.productEan = productEan;
            this.productName = productName;
            this.price = price;
            this.lowestPrice = null;
            this.externalId = null;
        }
    }

    private List<ApiRow> generateFakeData() {

        List<ApiRow> template = new ArrayList<>();
        template.add(new ApiRow("ean-1", "Red square imported", 100));
        template.add(new ApiRow("ean-2", "Blue square imported", 200));
        template.add(new ApiRow("ean-3", "Green square imported", 300));
        template.add(new ApiRow("ean-4", "Red triangle imported", 400));
        template.add(new ApiRow("ean-5", "Blue triangle imported", 500));
        template.add(new ApiRow("ean-6", "Green triangle imported", 600));
        template.add(new ApiRow("ean-7", "Red triangle imported", 700));
        template.add(new ApiRow("ean-8", "Blue circle imported", 800));
        template.add(new ApiRow("ean-9", "Green circle imported", 900));

        List<ApiRow> apiData = new ArrayList<>();
        for (var item : template) {
            if (skipRandom(5)) {
                continue;
            }
            item.externalId = UUID.randomUUID().toString();
            if (skipRandom(50)) {
                item.productName = null;
            }
            var price = item.price;
            item.price = (int)Math.round(0.9 * price + (0.2 * price) * random.nextFloat());
            if (skipRandom(80)) {
                item.lowestPrice = (int)Math.round(item.price * 1.1);
            }
            apiData.add(item);
        }

        return apiData;
    }

    private boolean skipRandom(int percent) {
        return random.nextInt(0, 101) <= percent;
    }

    private boolean notEqual(ApiRow item, Offer offer) {
        return ! (
            item.externalId.equals(offer.getExternalId())
            && item.productEan.equals(offer.getProductEan())
            && Objects.equals(item.productName, offer.getProductName())
            && item.price == offer.getPrice()
            && Objects.equals(item.lowestPrice, offer.getLowestPrice())
        );
    }
}
