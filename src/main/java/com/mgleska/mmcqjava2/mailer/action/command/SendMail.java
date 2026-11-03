package com.mgleska.mmcqjava2.mailer.action.command;

import com.mgleska.mmcqjava2.mailer.model.Dispatch;
import com.mgleska.mmcqjava2.mailer.model.DispatchRepository;
import com.mgleska.mmcqjava2.mailer.support.DispatchQueueItem;
import com.mgleska.mmcqjava2.mailer.support.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMail {

    public record ParamDto(
        String source,
        long sourceId,
        String recipient,
        String message
    ) {}

    public record ResultDto(
        long referenceId
    ) {}

    private final DispatchRepository dispatchRepository;
    private final RabbitTemplate rabbitTemplate;

    public SendMail(DispatchRepository dispatchRepository, RabbitTemplate rabbitTemplate) {
        this.dispatchRepository = dispatchRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResultDto handle(ParamDto dto) {

        var dispatch = new Dispatch(dto.source(), dto.sourceId(), dto.recipient(), dto.message());
        dispatchRepository.save(dispatch);

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, new DispatchQueueItem(dispatch.getId()));
        }
        catch (AmqpException e) {
            log.warn("Can't schedule dispatch on queue {}. Message: {} ", RabbitConfig.QUEUE_NAME,  e.getMessage());
        }

        return new ResultDto(dispatch.getId());
    }
}
