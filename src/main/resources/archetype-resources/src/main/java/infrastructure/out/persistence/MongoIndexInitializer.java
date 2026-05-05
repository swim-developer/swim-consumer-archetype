#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.out.persistence;

import com.github.swim_developer.framework.persistence.mongodb.AbstractMongoIndexInitializer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
public class MongoIndexInitializer extends AbstractMongoIndexInitializer {

    private static final String FIELD_RECEIVED_AT = "receivedAt";

    private final String databaseName;
    private final int eventTtlDays;

    protected MongoIndexInitializer() {
        super(null);
        this.databaseName = null;
        this.eventTtlDays = 90;
    }

    @Inject
    public MongoIndexInitializer(com.mongodb.client.MongoClient mongoClient,
                                 @ConfigProperty(name = "quarkus.mongodb.database") String databaseName,
                                 @ConfigProperty(name = "swim.${serviceName}-event.ttl-days", defaultValue = "90") int eventTtlDays) {
        super(mongoClient);
        this.databaseName = databaseName;
        this.eventTtlDays = eventTtlDays;
    }

    @Override
    public void onStart(@Observes StartupEvent event) {
        super.onStart(event);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected void defineIndexes(MongoDatabase database) {
        MongoCollection<Document> c = database.getCollection("${collectionPrefix}_events");

        createIndex(c, "subscriptionId_1", Indexes.ascending("subscriptionId"), null);
        createIndex(c, "kafkaStatus_1", Indexes.ascending("kafkaStatus"), null);
        createIndex(c, "inboxId_1", Indexes.ascending("inboxId"), null);
        createIndex(c, "receivedAt_ttl", Indexes.ascending(FIELD_RECEIVED_AT), ttlOptions(eventTtlDays));

        log.info("${serviceDisplayName} indexes configured (Event TTL: {} days)", eventTtlDays);
    }
}
