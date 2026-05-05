#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.in.rest.dto;

public record TopicDetailsResponse(
        String topicId,
        String topicName,
        String description,
        String publisherState
) {}
