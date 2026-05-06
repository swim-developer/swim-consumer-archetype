package ${package}.infrastructure.out.client;

import ${package}.domain.model.Subscription;
import ${package}.domain.model.command.SubscriptionCommand;
import ${package}.application.port.out.RemoteSubscriptionManagerPort;
import ${package}.framework.consumer.application.port.out.SwimRemoteFeatureQueryPort;
import ${package}.infrastructure.in.rest.dto.SubscriptionRequest;
import ${package}.infrastructure.in.rest.dto.SubscriptionResponse;
import ${package}.framework.consumer.infrastructure.out.client.AbstractSubscriptionManagerClientRegistry;
import ${package}.framework.application.model.ProviderConfiguration;
import ${package}.framework.application.model.SubscriptionStatusUpdate;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionManagerAdapter
        extends AbstractSubscriptionManagerClientRegistry<SubscriptionManagerRestClient>
        implements RemoteSubscriptionManagerPort, SwimRemoteFeatureQueryPort {

    @Override
    protected Class<SubscriptionManagerRestClient> getClientClass() {
        return SubscriptionManagerRestClient.class;
    }

    @Override
    public Subscription createSubscription(SubscriptionCommand command, ProviderConfiguration provider) {
        SubscriptionManagerRestClient client = getOrCreate(provider);
        SubscriptionRequest request = toRequest(command);
        SubscriptionResponse response = executeWithRetry(provider, "createSubscription",
                () -> client.createSubscription(request));
        return fromResponse(response, command.provider());
    }

    @Override
    public String updateSubscriptionStatus(String subscriptionId, String newStatus, ProviderConfiguration provider) {
        SubscriptionManagerRestClient client = getOrCreate(provider);
        SubscriptionStatusUpdate update = new SubscriptionStatusUpdate(newStatus);
        SubscriptionResponse response = client.updateSubscriptionStatus(subscriptionId, update);
        return response != null && response.subscriptionStatus() != null
                ? response.subscriptionStatus()
                : newStatus;
    }

    @Override
    public void deleteSubscription(String subscriptionId, ProviderConfiguration provider) {
        SubscriptionManagerRestClient client = getOrCreate(provider);
        client.deleteSubscription(subscriptionId);
    }

    @Override
    public String queryFeatures(String typeName, String filter, String validTime, ProviderConfiguration provider) {
        SubscriptionManagerRestClient client = getOrCreate(provider);
        return executeWithRetry(provider, "getFeatures",
                () -> client.getFeatures(typeName, filter, validTime));
    }

    @Override
    public String querySubscriptionStatus(String subscriptionId, ProviderConfiguration provider) {
        SubscriptionManagerRestClient client = getOrCreate(provider);
        SubscriptionResponse response = client.getSubscriptionDetails(subscriptionId);
        return response != null && response.subscriptionStatus() != null
                ? response.subscriptionStatus()
                : "UNKNOWN";
    }

    private static SubscriptionRequest toRequest(SubscriptionCommand command) {
        return new SubscriptionRequest(
                command.topic(),
                command.queueName(),
                command.provider(),
                command.description()
        );
    }

    private static Subscription fromResponse(SubscriptionResponse response, String providerId) {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(response.subscriptionId());
        subscription.setQueueName(response.queueName());
        subscription.setSubscriptionStatus(response.subscriptionStatus());
        subscription.setSubscriptionEnd(response.subscriptionEnd());
        subscription.setProviderId(providerId);
        return subscription;
    }
}
