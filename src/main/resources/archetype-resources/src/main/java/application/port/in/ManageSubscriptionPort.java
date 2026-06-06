package ${package}.application.port.in;

import ${package}.domain.model.Subscription;
import ${package}.domain.model.command.SubscriptionCommand;
import ${package}.framework.application.model.ProviderConfiguration;

import java.util.Optional;

public interface ManageSubscriptionPort {

    Subscription createSubscription(SubscriptionCommand command);

    Optional<Subscription> findBySubscriptionId(String subscriptionId);

    void deleteSubscriptionById(String subscriptionId);

    Subscription pauseSubscription(String subscriptionId);

    Subscription resumeSubscription(String subscriptionId);

    ProviderConfiguration resolveProvider(String providerId);
}
