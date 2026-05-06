package ${package}.application.usecase;

import ${package}.application.service.ProcessingMetrics;
import ${package}.application.port.out.SubscriptionStore;
import ${package}.application.service.EventDataValidator;
import ${package}.application.service.EventFilterService;
import ${package}.application.service.EventPersistenceService;
import ${package}.application.service.ProcessorCallbacks;
import ${package}.domain.model.Event;
import ${package}.infrastructure.out.xml.EventExtractor;
import ${package}.framework.consumer.application.messaging.processing.DefaultEventProcessorConfig;
import ${package}.framework.application.model.PreparedEvent;
import ${package}.framework.application.model.ProcessingContext;
import ${package}.framework.application.model.ProcessingOutcome;
import ${package}.framework.consumer.application.messaging.processing.EventProcessingOrchestrator;
import ${package}.framework.consumer.application.messaging.processing.EventProcessingOrchestratorDependencies;
import ${package}.framework.consumer.application.messaging.processing.SwimEventParser;
import ${package}.framework.consumer.application.messaging.processing.SwimEventProcessorCallbacks;
import ${package}.framework.application.port.in.SwimMessageInterceptor;
import ${package}.framework.application.port.out.SwimXmlUnmarshallerPort;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EventProcessingUseCase {

    // TODO: Replace Object with your JAXB root type (e.g., FficeMessageType, AixmBasicMessageType)
    private final EventProcessingOrchestrator<Event, Object> orchestrator;
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
        SwimEventProcessorCallbacks<Event> callbacks = new ProcessorCallbacks(metrics, subscriptionStore);
        this.orchestrator = new EventProcessingOrchestrator<>(new EventProcessingOrchestratorDependencies<>(
                processorConfig, parser, eventExtractor, validator, filterService,
                persistenceService, callbacks, meterRegistry, interceptorInstances));
    }

    public ProcessingOutcome processAndPersistSingleMessage(String subscriptionId, String queueName,
                                                            String amqpMessageId, String xml, int index) {
        return orchestrator.processMessage(new ProcessingContext(subscriptionId, queueName, amqpMessageId, xml, index, null));
    }

    public EventProcessingOrchestrator<Event, Object> eventProcessingOrchestrator() {
        return orchestrator;
    }

    public void batchPersistAndDispatch(List<PreparedEvent<Event>> batch) {
        persistenceService.batchPersistAndDispatch(batch);
    }

    public void markBatchAsProcessed(List<PreparedEvent<Event>> batch) {
        orchestrator.markBatchAsProcessed(batch);
    }
}
