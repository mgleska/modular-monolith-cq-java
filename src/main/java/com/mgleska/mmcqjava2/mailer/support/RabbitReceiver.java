package com.mgleska.mmcqjava2.mailer.support;

import com.mgleska.mmcqjava2.mailer.model.DispatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnWebApplication
@Slf4j
public class RabbitReceiver {

    private final DispatchRepository dispatchRepository;
    private final RemoteDeliveryService remoteDeliveryService;

    public RabbitReceiver(DispatchRepository dispatchRepository, RemoteDeliveryService remoteDeliveryService) {
        this.dispatchRepository = dispatchRepository;
        this.remoteDeliveryService = remoteDeliveryService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME, concurrency = RabbitConfig.RECEIVER_CONCURRENCY)
    public void processMessage(DispatchQueueItem item) throws InterruptedException, RetryException {

        log.info("Received message from RabbitMQ: {}", item);

        var dispatch = dispatchRepository.findById(item.refId()).orElse(null);

        if (dispatch == null) {
            log.warn("No dispatch found with id {}.", item.refId());
            return;
        }

        if (dispatch.getStatus() == DispatchStatusEnum.SENT) {
            log.warn("Dispatch with id {} already sent.", item.refId());
            return;
        }

        dispatch.setStatus(DispatchStatusEnum.SENDING);
        dispatchRepository.save(dispatch);

        var delivered = remoteDeliveryService.deliver(dispatch.getRecipient(), dispatch.getMessage());
        if (! delivered) {
            log.info("Failed delivery for dispatch with id: {}", item.refId());
            throw new RetryException();
        }

        dispatch.setStatus(DispatchStatusEnum.SENT);
        dispatchRepository.save(dispatch);

        log.info("End of processing for dispatch with id: {}", item.refId());
    }
}
