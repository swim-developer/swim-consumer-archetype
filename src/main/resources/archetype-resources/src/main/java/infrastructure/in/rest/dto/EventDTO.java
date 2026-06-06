package ${package}.infrastructure.in.rest.dto;

import java.time.Instant;

public record EventDTO(
        String id,
        String messageId,
        String subscriptionId,
        Instant receivedAt,
        String deliveryStatus
        // TODO: Add domain-specific fields matching your Event model
        // Example for FF-ICE: String fficeMessageType, String gufi
) {}
