#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.port.in;

import ${package}.domain.model.Subscription;
import ${package}.domain.model.command.SubscriptionCommand;
import com.github.swim_developer.framework.consumer.application.subscription.service.AbstractSubscriptionService;

public interface ManageSubscriptionPort {

    AbstractSubscriptionService<SubscriptionCommand, Subscription> getSubscriptionService();
}
