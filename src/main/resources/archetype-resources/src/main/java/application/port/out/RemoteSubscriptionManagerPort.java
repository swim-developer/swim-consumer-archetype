package ${package}.application.port.out;

import ${package}.domain.model.Subscription;
import ${package}.domain.model.command.SubscriptionCommand;
import ${package}.framework.application.model.ProviderConfiguration;

public interface RemoteSubscriptionManagerPort {

    Subscription createSubscription(SubscriptionCommand command, ProviderConfiguration provider);

    String updateSubscriptionStatus(String subscriptionId, String newStatus, ProviderConfiguration provider);

    void deleteSubscription(String subscriptionId, ProviderConfiguration provider);
}
