#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.service;

import com.github.swim_developer.framework.application.port.out.SwimEventFilterPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

// TODO: Replace Object with your domain-specific EventData type
@Slf4j
@ApplicationScoped
public class EventFilterService implements SwimEventFilterPort<Object> {

    @Override
    public boolean shouldProcess(String subscriptionId, Object eventData) {
        // TODO: Implement domain-specific filtering logic based on subscription filters
        return true;
    }
}
