#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.in.rest.dto;

import java.time.Instant;

public record EventDTO(
        String id,
        String messageId,
        String subscriptionId,
        Instant receivedAt
        // TODO: Add domain-specific event DTO fields
) {}
