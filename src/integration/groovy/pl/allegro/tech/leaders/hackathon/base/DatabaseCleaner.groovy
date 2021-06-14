package pl.allegro.tech.leaders.hackathon.base

import com.mongodb.reactivestreams.client.MongoClient
import groovy.transform.CompileStatic
import groovy.transform.SelfType
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

@CompileStatic
@SelfType(IntegrationSpec)
trait DatabaseCleaner {

    @Autowired
    MongoClient mongoClient

    void dropAllCollections() {
        Mono.from(mongoClient.getDatabase("hackserver").drop()).subscribe()
    }
}
