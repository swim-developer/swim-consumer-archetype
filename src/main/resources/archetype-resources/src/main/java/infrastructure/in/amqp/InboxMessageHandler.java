package ${package}.infrastructure.in.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import ${package}.application.usecase.EventProcessingUseCase;
import ${package}.domain.model.Event;
import ${package}.infrastructure.out.xml.XmlEnvelopeParser;
import ${package}.extension.inbox.reader.kafka.AbstractKafkaInboxReader;
import ${package}.framework.application.model.PreparedEvent;
import ${package}.framework.application.model.ProcessingOutcome;
import ${package}.framework.infrastructure.out.messaging.InboxEnvelope;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.kafka.KafkaRecordBatch;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.List;
import java.util.concurrent.CompletionStage;

@Slf4j
@ApplicationScoped
public class InboxMessageHandler extends AbstractKafkaInboxReader {

    private final EventProcessingUseCase eventProcessor;
    private final XmlEnvelopeParser envelopeParser;

    protected InboxMessageHandler() {
        this(null, null, null, null);
    }

    @Inject
    public InboxMessageHandler(ObjectMapper objectMapper,
                               MeterRegistry meterRegistry,
                               EventProcessingUseCase eventProcessor,
                               XmlEnvelopeParser envelopeParser) {
        super(objectMapper, meterRegistry);
        this.eventProcessor = eventProcessor;
        this.envelopeParser = envelopeParser;
    }

    @Incoming("in-${serviceName}-inbox")
    @Blocking
    public CompletionStage<Void> onInboxBatch(KafkaRecordBatch<String, String> batch) {
        List<PreparedEvent<Event>> prepared = prepareBatch(batch, eventProcessor.eventProcessingOrchestrator());

        if (!prepared.isEmpty()) {
            eventProcessor.batchPersistAndDispatch(prepared);
            eventProcessor.markBatchAsProcessed(prepared);
        }

        processedCounter.increment(prepared.size());
        return batch.ack();
    }

    @Override
    public List<String> extractMessages(String rawPayload) {
        return envelopeParser.splitEnvelope(rawPayload);
    }

    @WithSpan("${collectionPrefix}.consumer.event.process")
    @Override
    public void processSingleMessage(InboxEnvelope envelope, String xmlPayload, int index) {
        Span.current().setAttribute("${collectionPrefix}.subscription", envelope.subscriptionId());
        Span.current().setAttribute("${collectionPrefix}.queue", envelope.queueName());

        ProcessingOutcome outcome = eventProcessor.processAndPersistSingleMessage(
                envelope.subscriptionId(),
                envelope.queueName(),
                envelope.amqpMessageId(),
                xmlPayload,
                index);
        Span.current().setAttribute("${collectionPrefix}.outcome", outcome.name());
    }

    @Override
    public String getMetricPrefix() {
        return "${collectionPrefix}";
    }
}
