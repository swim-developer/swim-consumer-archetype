package ${package}.infrastructure.out.mapper;

import ${package}.framework.consumer.infrastructure.out.dlq.DeadLetterMessage;
import ${package}.domain.model.Event;
import ${package}.infrastructure.in.rest.dto.EventDTO;
import ${package}.framework.infrastructure.out.messaging.DlqMessageDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionMapper {

    public EventDTO toDTO(Event event) {
        // TODO: Map domain-specific fields from Event to EventDTO
        return new EventDTO(
                event.getId() != null ? event.getId().toHexString() : null,
                event.getMessageId(),
                event.getSubscriptionId(),
                event.getReceivedAt(),
                event.getDeliveryStatus() != null ? event.getDeliveryStatus().name() : null
        );
    }

    public DlqMessageDTO toDTO(DeadLetterMessage dlq) {
        return new DlqMessageDTO(
                dlq.getId(),
                dlq.getAmqpMessageId(),
                dlq.getMessageIndex(),
                dlq.getSubscriptionId(),
                dlq.getQueueName(),
                dlq.getErrorType(),
                dlq.getErrorMessage(),
                dlq.getRawPayload(),
                dlq.getReceivedAt(),
                dlq.getFailedAt()
        );
    }
}
