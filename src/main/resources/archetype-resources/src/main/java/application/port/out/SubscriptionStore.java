#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.port.out;

import ${package}.domain.model.Subscription;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionStore {

    void persistSubscription(Subscription subscription);

    void updateSubscription(Subscription subscription);

    void updateStatus(String subscriptionId, String newStatus);

    Optional<Subscription> findBySubscriptionId(String subscriptionId);

    List<Subscription> findAllSubscriptions();

    List<Subscription> findActiveSubscriptions();

    List<Subscription> findBySubscriptionEndBefore(Instant threshold);

    Optional<Subscription> findByConfigHash(String configHash);

    long countSubscriptions();

    void deleteAllSubscriptions();
}
