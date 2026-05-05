#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.in.rest;

import com.github.swim_developer.framework.consumer.infrastructure.in.rest.AbstractFeatureResource;
import com.github.swim_developer.framework.consumer.application.port.in.QueryFeaturesPort;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/swim/v1/features")
@Tag(name = "${serviceDisplayName} Features")
public class FeatureResource extends AbstractFeatureResource {

    @Inject
    public FeatureResource(QueryFeaturesPort queryFeaturesPort) {
        super(queryFeaturesPort);
    }
}
