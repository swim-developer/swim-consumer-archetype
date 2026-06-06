package ${package}.infrastructure.out.xml;

import ${package}.domain.model.Event;
import ${package}.framework.application.port.out.SwimEventExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class EventExtractor implements SwimEventExtractor<Event, Object> {

    @Override
    public String getTypeLabel(Event event) {
        // TODO: Return the domain-specific event type label
        return "unknown";
    }

    @Override
    public List<Optional<Event>> extract(Object rootElement) {
        // TODO: Cast rootElement to your JAXB type and extract domain fields into Event
        if (rootElement == null) {
            return List.of(Optional.empty());
        }
        Event event = new Event();
        return List.of(Optional.of(event));
    }
}
