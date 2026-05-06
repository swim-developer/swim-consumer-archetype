package ${package}.application.service;

import ${package}.domain.model.Event;
import ${package}.framework.application.port.out.SwimDeadLetterPort;
import ${package}.framework.application.port.out.SwimSubscriptionFilterPort;
import ${package}.framework.consumer.application.messaging.processing.AbstractEventFilterService;
import ${package}.framework.consumer.application.messaging.processing.FilterRule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EventFilterService extends AbstractEventFilterService<Event> {

    @Inject
    public EventFilterService(SwimSubscriptionFilterPort filterCache,
                              SwimDeadLetterPort deadLetterService) {
        super(filterCache, deadLetterService);
    }

    @Override
    protected List<FilterRule<Event>> buildFilterRules(Event event) {
        return List.of();
    }
}
