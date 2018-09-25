package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationRecord
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository

class MongoExperimentsGroupRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should use cached ExperimentGroupRepository "(){
      expect:
      experimentGroupRepository instanceof CachedExperimentGroupRepository
    }

    def "should save and get an ExperimentGroup from the repository"() {
        given:
        def id = UUID.randomUUID().toString()
        def group = new ExperimentGroup(id, 'salt', ['exp1', 'exp2'],
                new AllocationTable([new AllocationRecord('exp1', 'v1', 0, 50),
                                     new AllocationRecord('exp2', 'v2', 50, 100)]))

        when:
        experimentGroupRepository.save(group)
        def loaded = experimentGroupRepository.findById(id).get()

        then: 'loaded object should be equal to the saved object'
        loaded.id == id
        loaded.salt == group.salt
        loaded.experiments == group.experiments
        loaded.allocationTable == group.allocationTable

        when:
        Document doc = mongoTemplate.findById(id, Document, "experimentGroup")

        then: 'BSON representation should looks like this'
        doc.getString('_id') ==  id
        doc.get('salt') == 'salt'
        doc.get('experiments') == ['exp1','exp2']
        doc.get('allocationTable').get('records') == [
                [
                        experimentId: 'exp1',
                        variant: 'v1',
                        range: [
                            from: 0,
                            to: 50
                        ]
                ],
                [
                        experimentId: 'exp2',
                        variant: 'v2',
                        range: [
                            from: 50,
                            to: 100
                        ]
                ]
        ]
    }
}
