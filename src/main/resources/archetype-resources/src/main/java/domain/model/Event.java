package ${package}.domain.model;

import ${package}.framework.application.model.OutboxDeliveryStatus;
import ${package}.framework.domain.model.SwimOutboxEvent;
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
    private String queueName;
    private String contentHash;
    private String rawPayload;
    private Instant receivedAt;
    private OutboxDeliveryStatus deliveryStatus;
    private int dispatchRetryCount;
    private Instant dispatchedAt;

    // TODO: Add domain-specific fields extracted from your data model
    // Example for FF-ICE: private String fficeMessageType; private String gufi;
    // Example for DNOTAM: private String scenario; private String eventType;

    @Override
    public int getOutboxRetryCount() { return dispatchRetryCount; }

    @Override
    public void setOutboxRetryCount(int count) { this.dispatchRetryCount = count; }
}
