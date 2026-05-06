package ${package}.infrastructure.out.xml;

import ${package}.framework.application.port.out.SwimXmlUnmarshallerPort;
import ${package}.framework.domain.exception.XmlValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class JaxbUnmarshallerPool implements SwimXmlUnmarshallerPort<Object> {

    // TODO: Initialize your data model's UnmarshallerPool (e.g., FficeUnmarshallerPool)
    // and delegate unmarshalAndValidate to it.

    @Override
    public Object unmarshalAndValidate(String xml) throws XmlValidationException {
        throw new UnsupportedOperationException(
                "Implement unmarshalAndValidate using your data model's UnmarshallerPool");
    }
}
