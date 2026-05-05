#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.out.mapper;

import com.github.swim_developer.framework.consumer.infrastructure.out.dlq.DeadLetterMessage;
import ${package}.domain.model.Event;
import ${package}.infrastructure.in.rest.dto.EventDTO;
import com.github.swim_developer.framework.infrastructure.out.messaging.DlqMessageDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionMapper {

    // TODO: Map domain-specific fields from Event to EventDTO
    public EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getId() != null ? event.getId().toHexString() : null,
                event.getMessageId(),
                event.getSubscriptionId(),
                event.getReceivedAt()
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
