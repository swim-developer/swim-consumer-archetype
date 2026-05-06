package ${package}.infrastructure.out.subscription;

import ${package}.domain.model.Subscription;
import ${package}.application.port.out.SubscriptionStore;
import ${package}.infrastructure.out.client.SubscriptionManagerAdapter;
import ${package}.infrastructure.out.client.SubscriptionManagerRestClient;
import ${package}.infrastructure.in.rest.dto.SubscriptionResponse;
import ${package}.framework.consumer.infrastructure.out.config.provider.ProviderConfigParser;
import ${package}.framework.application.model.ProviderConfiguration;
import ${package}.framework.domain.model.SubscriptionRenewalInfo;
import ${package}.framework.domain.exception.SubscriptionRenewalException;
import ${package}.framework.domain.model.SubscriptionStatus;
import ${package}.framework.application.port.out.SubscriptionRenewalStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ${servicePrefix}SubscriptionRenewalStrategy implements SubscriptionRenewalStrategy {

    private final SubscriptionStore subscriptionStore;
    private final SubscriptionManagerAdapter smClientRegistry;
    private final ProviderConfigParser providerConfigParser;

    @Inject
    public ${servicePrefix}SubscriptionRenewalStrategy(SubscriptionStore subscriptionStore,
                                            SubscriptionManagerAdapter smClientRegistry,
                                            ProviderConfigParser providerConfigParser) {
        this.subscriptionStore = subscriptionStore;
        this.smClientRegistry = smClientRegistry;
        this.providerConfigParser = providerConfigParser;
    }

    @Override
    public List<SubscriptionRenewalInfo> findSubscriptionsNearExpiry(Instant threshold) {
        return subscriptionStore.findBySubscriptionEndBefore(threshold)
                .stream()
                .filter(sub -> SubscriptionStatus.ACTIVE.name().equals(sub.getSubscriptionStatus()))
                .map(sub -> new SubscriptionRenewalInfo(sub.getSubscriptionId(), sub.getSubscriptionEnd()))
                .toList();
    }

    @Override
    public void renewSubscription(String subscriptionId) throws SubscriptionRenewalException {
        log.info("Renewing subscription: {}", subscriptionId);

        Subscription subscription = subscriptionStore.findBySubscriptionId(subscriptionId)
                .orElseThrow(() -> new IllegalStateException("Subscription not found: " + subscriptionId));

        SubscriptionManagerRestClient client = resolveSmClient(subscription.getProviderId());
        SubscriptionResponse response = client.renewSubscription(subscriptionId);

        subscription.setSubscriptionEnd(response.subscriptionEnd());
        subscriptionStore.updateSubscription(subscription);

        log.info("Subscription renewed - ID: {}, New end: {}", subscriptionId, response.subscriptionEnd());
    }

    private SubscriptionManagerRestClient resolveSmClient(String providerId) {
        ProviderConfiguration provider = providerConfigParser.findByProviderId(providerId)
                .orElseThrow(() -> new IllegalStateException("Provider not configured: " + providerId));
        return smClientRegistry.getOrCreate(provider);
    }
}
