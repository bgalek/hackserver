package pl.allegro.experiments.chi.chiserver.infrastructure

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration

@Configuration
class FongoConfiguration extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "demo-test";
    }

    @Bean
    @Override
    public Mongo mongo() {
        // uses fongo for in-memory tests
        return new Fongo("mongo-test").getMongo();
    }
}