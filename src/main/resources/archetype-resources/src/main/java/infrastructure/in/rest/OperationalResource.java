package ${package}.infrastructure.in.rest;

import ${package}.framework.consumer.infrastructure.in.rest.AbstractOperationalResource;
import ${package}.framework.consumer.application.port.out.DeadLetterStore;
import ${package}.framework.consumer.application.port.in.ConsumerStatisticsPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/swim/v1/operational")
@Tag(name = "${serviceDisplayName} Operational")
public class OperationalResource extends AbstractOperationalResource {

    @Inject
    public OperationalResource(DeadLetterStore dlqRepository, ConsumerStatisticsPort statisticsPort) {
        super(dlqRepository, statisticsPort);
    }
}
