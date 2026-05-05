#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.infrastructure.out.persistence.repository;

import ${package}.infrastructure.out.persistence.document.SubscriptionDocument;
import com.github.swim_developer.framework.persistence.mongodb.AbstractMongoSubscriptionRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionDocumentRepository extends AbstractMongoSubscriptionRepository<SubscriptionDocument> {
}
