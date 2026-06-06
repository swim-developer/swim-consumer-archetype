package ${package}.infrastructure.in.rest.dto;

public record TopicDetailsResponse(
        String topicId,
        String topicName,
        String description,
        String publisherState
) {}
