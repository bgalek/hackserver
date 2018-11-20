package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository

class MongoExperimentTagRepositoryIntegrationSpec extends BaseIntegrationSpec {
    @Autowired
    ExperimentTagRepository experimentTagRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should save and get an ExperimentTag from the repository"() {
        given:
        def tag = new ExperimentTag(UUID.randomUUID().toString())

        when:
        experimentTagRepository.save(tag)
        def loaded = experimentTagRepository.get(tag.id).get()

        then:
        loaded.id == tag.id

        when:
        Document doc = mongoTemplate.findById(tag.id, Document, "experimentTags")

        then:
        doc.getString('_id') == tag.id
    }
}
