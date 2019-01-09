package pl.allegro.tech.leaders.hackathon.base

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate

@CompileStatic
@SelfType(IntegrationSpec)
trait DatabaseCleaner {
    @Autowired
    MongoTemplate mongoTemplate

    void dropAllCollections() {
        mongoTemplate.getCollectionNames()
                .forEach { mongoTemplate.dropCollection(it as String) }
    }
}
