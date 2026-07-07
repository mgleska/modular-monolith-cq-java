package com.mgleska.mmcqjava2.offer.action.command;

import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.shared.exception.AppEntityVersionException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ChangeVisibilityCmd {

    public record ParamDto(
        @NotNull Integer id,
        @NotNull Integer version,
        @NotNull Boolean visible
    ){}

    private final OfferRepository offerRepository;

    public ChangeVisibilityCmd(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Transactional
    public void handle(ParamDto dto) {

        var offer = offerRepository.findWithLockById(dto.id);
        if (offer == null) {
            throw new AppValidationException("id", "Offer not found");
        }

        if (offer.getVersion() != dto.version) {
            throw new AppEntityVersionException();
        }

        offer.setVisible(dto.visible);
        offer.setVersion(offer.getVersion() + 1);
        offerRepository.save(offer);
    }
}
