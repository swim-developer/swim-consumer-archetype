#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.in.rest.dto;

public record SubscriptionRequest(
        String topic,
        String queueName,
        String provider,
        String description
        // TODO: Add domain-specific subscription request fields
) {}
