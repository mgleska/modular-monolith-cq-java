package com.mgleska.mmcqjava2.mailer.action.query;

import com.mgleska.mmcqjava2.mailer.action.enums.DeliveryStatusEnum;
import com.mgleska.mmcqjava2.mailer.model.DispatchRepository;
import com.mgleska.mmcqjava2.mailer.support.DispatchStatusEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetDeliveryStatusForIds {

    private final DispatchRepository dispatchRepository;

    public GetDeliveryStatusForIds(DispatchRepository dispatchRepository) {
        this.dispatchRepository = dispatchRepository;
    }

    public @NotNull Map<Long, DeliveryStatusEnum> handle(List<Long> referenceIds) {
        var list = dispatchRepository.findByIds(referenceIds);
        if (list.isEmpty()) {
            return Map.of();
        }

        var result = new HashMap<Long, DeliveryStatusEnum>();
        for(var item : list) {
            result.put(item.id(), item.status() == DispatchStatusEnum.SENT ? DeliveryStatusEnum.DELIVERED : DeliveryStatusEnum.IN_PROGRESS);
        }

        return result;
    }
}
