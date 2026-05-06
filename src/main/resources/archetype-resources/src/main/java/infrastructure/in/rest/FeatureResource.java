package ${package}.infrastructure.in.rest;

import ${package}.framework.consumer.infrastructure.in.rest.AbstractFeatureResource;
import ${package}.framework.consumer.application.port.in.SwimQueryFeaturesPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/swim/v1/features")
@Tag(name = "${serviceDisplayName} Features")
public class FeatureResource extends AbstractFeatureResource {

    @Inject
    public FeatureResource(SwimQueryFeaturesPort queryFeaturesPort) {
        super(queryFeaturesPort);
    }
}
