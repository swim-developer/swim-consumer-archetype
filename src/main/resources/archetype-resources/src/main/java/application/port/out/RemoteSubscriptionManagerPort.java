#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.port.out;

import ${package}.domain.model.command.SubscriptionCommand;
import com.github.swim_developer.framework.consumer.application.port.out.SubscriptionManagerActions;

public interface RemoteSubscriptionManagerPort extends SubscriptionManagerActions<SubscriptionCommand> {
}
