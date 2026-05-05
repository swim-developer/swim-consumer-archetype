#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.usecase;

import ${package}.application.service.ProcessingMetrics;
import ${package}.application.port.out.SubscriptionStore;
import ${package}.application.service.EventDataValidator;
import ${package}.application.service.EventFilterService;
import ${package}.application.service.EventPersistenceService;
import ${package}.application.service.ProcessorCallbacks;
import ${package}.infrastructure.out.xml.EventExtractor;
import com.github.swim_developer.framework.consumer.application.messaging.processing.DefaultEventProcessorConfig;
import com.github.swim_developer.framework.application.model.PreparedEvent;
import com.github.swim_developer.framework.application.model.ProcessingContext;
import com.github.swim_developer.framework.application.model.ProcessingOutcome;
import com.github.swim_developer.framework.consumer.application.messaging.processing.EventProcessingOrchestrator;
import com.github.swim_developer.framework.consumer.application.messaging.processing.EventProcessingOrchestratorDependencies;
import com.github.swim_developer.framework.consumer.application.messaging.processing.SwimEventParser;
import com.github.swim_developer.framework.consumer.application.messaging.processing.SwimEventProcessorCallbacks;
import com.github.swim_developer.framework.application.port.in.SwimMessageInterceptor;
import com.github.swim_developer.framework.application.port.out.SwimXmlUnmarshallerPort;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;

// TODO: Replace Object (2nd type param) with your JAXB root type (e.g., AIXMBasicMessageType)
// TODO: Replace Object (EventData) with your domain-specific extracted data type
@ApplicationScoped
public class EventProcessingUseCase {

    private final EventProcessingOrchestrator<Object, Object> orchestrator;
    private final EventPersistenceService persistenceService;

    @Inject
    public EventProcessingUseCase(
            DefaultEventProcessorConfig processorConfig,
            SwimXmlUnmarshallerPort<Object> jaxbPool,
            EventExtractor eventExtractor,
            EventDataValidator validator,
            EventFilterService filterService,
            EventPersistenceService persistenceService,
            ProcessingMetrics metrics,
            MeterRegistry meterRegistry,
            SubscriptionStore subscriptionStore,
            @Any Instance<SwimMessageInterceptor> interceptorInstances) {
        this.persistenceService = persistenceService;
        SwimEventParser<Object> parser = jaxbPool::unmarshalAndValidate;
        SwimEventProcessorCallbacks<Object> callbacks = new ProcessorCallbacks(metrics, subscriptionStore);
        this.orchestrator = new EventProcessingOrchestrator<>(new EventProcessingOrchestratorDependencies<>(
                processorConfig, parser, eventExtractor, validator, filterService,
                persistenceService, callbacks, meterRegistry, interceptorInstances));
    }

    public ProcessingOutcome processAndPersistSingleMessage(String subscriptionId, String queueName,
                                                            String amqpMessageId, String xml, int index) {
        return orchestrator.processMessage(new ProcessingContext(subscriptionId, queueName, amqpMessageId, xml, index, null));
    }

    public EventProcessingOrchestrator<Object, Object> eventProcessingOrchestrator() {
        return orchestrator;
    }

    public void batchPersistAndDispatch(List<PreparedEvent<?>> batch) {
        persistenceService.batchPersistAndDispatch((List) batch);
    }

    public void markBatchAsProcessed(List<PreparedEvent<?>> batch) {
        orchestrator.markBatchAsProcessed((List) batch);
    }
}
