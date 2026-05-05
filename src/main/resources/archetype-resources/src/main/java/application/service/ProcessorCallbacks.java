#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.service;

import ${package}.application.port.out.SubscriptionStore;
import ${package}.domain.model.Subscription;
import com.github.swim_developer.framework.application.model.ProcessingContext;
import com.github.swim_developer.framework.consumer.application.messaging.processing.SwimEventProcessorCallbacks;
import com.github.swim_developer.framework.consumer.application.messaging.processing.SwimEventProcessorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

// TODO: Replace Object with your domain-specific EventData type
@Slf4j
public class ProcessorCallbacks implements SwimEventProcessorCallbacks<Object> {

    private final ProcessingMetrics metrics;
    private final SubscriptionStore subscriptionStore;

    public ProcessorCallbacks(ProcessingMetrics metrics, SubscriptionStore subscriptionStore) {
        this.metrics = metrics;
        this.subscriptionStore = subscriptionStore;
    }

    @Override
    public boolean preProcess(ProcessingContext ctx) {
        Optional<Subscription> sub = subscriptionStore.findBySubscriptionId(ctx.subscriptionId());
        if (sub.isPresent() && "PAUSED".equals(sub.get().getSubscriptionStatus())) {
            log.warn("PAUSED_SUBSCRIPTION_DISCARD: SubscriptionId={}, MessageId={}",
                    ctx.subscriptionId(), ctx.amqpMessageId());
            return true;
        }
        return false;
    }

    @Override
    public void onDuplicateDetected(ProcessingContext ctx, String contentHash) {
        metrics.incrementDuplicate();
    }

    @Override
    public void onExtractionFailure(ProcessingContext ctx, SwimEventProcessorConfig config) {
        metrics.incrementInvalid("INVALID");
        log.error("Invalid ${serviceDisplayName} message - MessageId: {}", ctx.compositeMessageId());
        // TODO: Implement domain-specific extraction failure handling
    }

    @Override
    public void onValidationFailure(ProcessingContext ctx, Exception e) {
        log.error("Problematic XML (first 500 chars): {}",
                ctx.xmlPayload().length() > 500 ? ctx.xmlPayload().substring(0, 500) : ctx.xmlPayload());
    }
}
