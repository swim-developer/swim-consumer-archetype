package ${package}.infrastructure.out.persistence.repository;

import ${package}.infrastructure.out.persistence.document.SubscriptionDocument;
import ${package}.framework.persistence.mongodb.AbstractMongoSubscriptionRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionDocumentRepository extends AbstractMongoSubscriptionRepository<SubscriptionDocument> {
}
