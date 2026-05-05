#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.service;

import ${package}.application.port.out.EventStore;
import ${package}.domain.model.Event;
import com.github.swim_developer.framework.application.model.OutboxDeliveryStatus;
import com.github.swim_developer.framework.application.model.ProcessingContext;
import com.github.swim_developer.framework.application.port.out.SwimDeadLetterPort;
import com.github.swim_developer.framework.consumer.application.messaging.outbox.OutboxRouterFanOut;
import com.github.swim_developer.framework.consumer.application.messaging.processing.AbstractEventPersistenceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class EventPersistenceService extends AbstractEventPersistenceService<Object, Event> {

    private final EventStore repository;

    @Inject
    public EventPersistenceService(EventStore repository,
                                   OutboxRouterFanOut outboxRouterFanOut,
                                   SwimDeadLetterPort deadLetterService) {
        super(outboxRouterFanOut, deadLetterService);
        this.repository = repository;
    }

    // TODO: Replace Object with your domain-specific EventData type
    // TODO: Map extracted event data fields to Event entity
    @Override
    protected Event assembleEntity(ProcessingContext ctx, Object eventData, String contentHash) {
        Event event = new Event();
        event.setSubscriptionId(ctx.subscriptionId());
        event.setMessageId(ctx.compositeMessageId());
        event.setRawXml(ctx.xmlPayload());
        event.setContentHash(contentHash);
        event.setKafkaStatus(OutboxDeliveryStatus.SENT);
        event.setReceivedAt(Instant.now());
        return event;
    }

    @Override
    protected void persistEntity(Event entity) { repository.persist(entity); }

    @Override
    protected void persistEntities(List<Event> entities) { repository.persist(entities); }

    @Override
    protected void updateEntity(Event entity) { repository.update(entity); }

    @Override
    protected String getServicePrefix() { return "${serviceDisplayName}"; }
}
