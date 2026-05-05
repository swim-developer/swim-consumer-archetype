#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.service;

import com.github.swim_developer.framework.application.port.out.SwimPayloadValidator;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

// TODO: Replace Object with your domain-specific EventData type
@Slf4j
@ApplicationScoped
public class EventDataValidator implements SwimPayloadValidator<Object> {

    @Override
    public boolean validate(Object eventData) {
        if (eventData == null) {
            log.warn("Null event data received");
            return false;
        }
        // TODO: Implement domain-specific validation rules
        return true;
    }
}
