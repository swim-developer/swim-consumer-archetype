#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.out.xml;

import com.github.swim_developer.framework.application.port.out.SwimXmlUnmarshallerPort;
import com.github.swim_developer.framework.domain.exception.XmlValidationException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

// TODO: Replace Object with your JAXB root type (e.g., AIXMBasicMessageType or FlightMessageType)
// TODO: Initialize the JAXB unmarshaller pool from your data model module
@Slf4j
@ApplicationScoped
public class JaxbUnmarshallerPool implements SwimXmlUnmarshallerPort<Object> {

    @PostConstruct
    void logInitialization() {
        log.info("${serviceDisplayName} JAXB unmarshaller pool initialized");
    }

    @Override
    public Object unmarshalAndValidate(String xml) throws XmlValidationException {
        // TODO: Implement XML unmarshalling using your JAXB data model
        // Example for AIXM: return new AixmUnmarshallerPool().unmarshalAndValidate(xml);
        // Example for FIXM: return new FixmUnmarshallerPool().unmarshalAndValidate(xml);
        throw new UnsupportedOperationException("Implement ${serviceDisplayName} JAXB unmarshalling");
    }
}
