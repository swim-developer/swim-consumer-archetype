#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.in.rest.dto;

public record SubscriptionResponse(
        String subscriptionId,
        String subscriptionStatus,
        String queueName
) {}
