package ${package}.application.service;

import ${package}.domain.model.Event;
import ${package}.framework.application.model.ProcessingContext;
import ${package}.framework.consumer.application.messaging.processing.SwimEventValidator;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class EventDataValidator implements SwimEventValidator<Event> {

    @Override
    public void validateExtractedData(ProcessingContext ctx, Event event) {
        // TODO: Add domain-specific validation logic
        // Example: if (event.getEventType() == null) log.warn("Event type missing");
    }
}
