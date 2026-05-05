#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain.model;

import com.github.swim_developer.framework.application.model.OutboxDeliveryStatus;
import com.github.swim_developer.framework.domain.model.SwimOutboxEvent;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "${collectionPrefix}_events")
public class Event implements SwimOutboxEvent {

    private ObjectId id;
    private String messageId;
    private String subscriptionId;
    private String contentHash;
    private String rawXml;
    private Instant receivedAt;
    private OutboxDeliveryStatus kafkaStatus;

    // TODO: Add domain-specific event fields (extracted data)

    @Override
    public String getOutboxMessageId() {
        return messageId;
    }

    @Override
    public OutboxDeliveryStatus getOutboxStatus() {
        return kafkaStatus;
    }

    @Override
    public void setOutboxStatus(OutboxDeliveryStatus status) {
        this.kafkaStatus = status;
    }
}
