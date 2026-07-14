package com.mgleska.mmcqjava2.offer.action.command;

import com.mgleska.mmcqjava2.offer.model.Offer;
import com.mgleska.mmcqjava2.offer.model.OfferRepository;
import com.mgleska.mmcqjava2.shared.exception.AppEntityVersionException;
import com.mgleska.mmcqjava2.shared.exception.AppValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeVisibilityCmdTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private ChangeVisibilityCmd changeVisibilityCmd;

    @Test
    void changesVisibilityAndIncrementsVersion() {
        var offer = new Offer();
        offer.setVersion(1);
        offer.setVisible(true);
        when(offerRepository.findWithLockById(5)).thenReturn(offer);

        changeVisibilityCmd.handle(new ChangeVisibilityCmd.ParamDto(5, 1, false));

        var captor = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepository).save(captor.capture());
        var savedOffer = captor.getValue();
        assertThat(savedOffer.isVisible()).isFalse();
        assertThat(savedOffer.getVersion()).isEqualTo(2);
    }

    @Test
    void throwsWhenOfferNotFound() {
        when(offerRepository.findWithLockById(5)).thenReturn(null);
        var dto = new ChangeVisibilityCmd.ParamDto(5, 1, false);

        assertThatThrownBy(() -> changeVisibilityCmd.handle(dto))
            .isInstanceOf(AppValidationException.class)
            .satisfies(ex -> assertThat(((AppValidationException) ex).getField()).isEqualTo("id"));

        verify(offerRepository, never()).save(any());
    }

    @Test
    void throwsWhenVersionMismatch() {
        var offer = new Offer();
        offer.setVersion(2);
        when(offerRepository.findWithLockById(5)).thenReturn(offer);
        var dto = new ChangeVisibilityCmd.ParamDto(5, 1, false);

        assertThatThrownBy(() -> changeVisibilityCmd.handle(dto))
            .isInstanceOf(AppEntityVersionException.class);

        verify(offerRepository, never()).save(any());
    }
}
