package ${package}.infrastructure.out.persistence;

import ${package}.domain.model.Event;
import io.quarkus.mongodb.panache.common.ProjectionFor;
import lombok.Getter;
import lombok.Setter;

@ProjectionFor(Event.class)
@Getter
@Setter
public class EventHashProjection {
    private String subscriptionId;
    private String contentHash;
}
