#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.application.port.out;

import ${package}.domain.model.Event;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventStore {

    List<Event> listAllDomain();

    Optional<Event> findById(String id);

    Optional<Event> findByMessageId(String messageId);

    boolean existsByContentHash(String contentHash);

    long countAll();

    void persist(Event event);

    void persist(List<Event> events);

    void update(Event event);

    // TODO: Add domain-specific query methods
}
