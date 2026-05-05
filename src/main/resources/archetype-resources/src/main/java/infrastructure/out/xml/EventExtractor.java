#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.out.xml;

import com.github.swim_developer.framework.domain.model.SwimEventExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

// TODO: Replace Object (1st type param) with your domain-specific EventData type
// TODO: Replace Object (2nd type param) with your JAXB root type (e.g., AIXMBasicMessageType or FlightMessageType)
@Slf4j
@ApplicationScoped
public class EventExtractor implements SwimEventExtractor<Object, Object> {

    @Override
    public Optional<Object> extract(Object jaxbRoot) {
        // TODO: Implement domain-specific extraction logic
        // Parse JAXB root object and extract relevant event data
        throw new UnsupportedOperationException("Implement ${serviceDisplayName} event extraction from JAXB model");
    }
}
