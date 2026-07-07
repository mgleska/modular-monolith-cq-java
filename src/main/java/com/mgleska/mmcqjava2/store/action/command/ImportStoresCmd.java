package com.mgleska.mmcqjava2.store.action.command;

import com.mgleska.mmcqjava2.store.model.Store;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportStoresCmd {

    private final EntityManager em;

    public ImportStoresCmd(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void handle() {

        record ApiRow(
            String rid,
            String name,
            String address
        ) {}

        // Fake import from external API
        List<ApiRow> apiData = new ArrayList<>();
        apiData.add(new ApiRow("r001","Poznań", "ul. Bałtycka 1"));
        apiData.add(new ApiRow("r002","Luboń", "ul. Poznańska 1"));
        apiData.add(new ApiRow("r003","Czerwonak", "ul. Poznańska 2"));
        apiData.add(new ApiRow("r004","Kórnik", "ul. Poznańska 1"));
        apiData.add(new ApiRow("r005","Mosina", "ul. Poznańska 1"));

        var ids = apiData
            .stream()
            .map(item  -> item.rid)
            .toList();


        // undelete if required
        em.createNativeQuery(
            "UPDATE " + Store.TABLE + " SET is_deleted = 0, updated_at = NOW() WHERE is_deleted = 1 AND external_id IN (:ids)"
            )
            .setParameter("ids", ids)
            .executeUpdate();

        // delete missing
        em.createNativeQuery(
            "UPDATE " + Store.TABLE + " SET is_deleted = 1, updated_at = NOW() WHERE is_deleted = 0 AND external_id NOT IN (:ids)"
            )
            .setParameter("ids", ids)
            .executeUpdate();

        var dbAll = em.createQuery("SELECT s FROM Store s", Store.class)
                .getResultStream()
                .collect(Collectors.toMap(Store::getExternalId, s -> s));

        for (var item : apiData) {
            var store = dbAll.get(item.rid);
            if (store == null) {
                em.persist(new Store(item.rid, item.name, item.address));
                continue;
            }
            if (! (item.rid.equals(store.getExternalId())
                && item.name.equals(store.getName())
                && item.address.equals(store.getAddress()))
            ) {
                store.setExternalId(item.rid);
                store.setName(item.name);
                store.setAddress(item.address);
                em.persist(store);
            }
        }
        em.flush();
    }

}
