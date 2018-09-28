package pl.allegro.experiments.chi.chiserver.infrastructure.experiments


import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
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
                new AllocationTable().allocate ('exp1', ['v1','base'], 10)
                                     .allocate ('exp2', ['v1','base'], 10))

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
                            from:  0,
                            to:   10
                        ]
                ],
                [
                        experimentId: 'exp2',
                        variant: 'v1',
                        range: [
                            from: 10,
                            to:   20
                        ]
                ],
                [
                        experimentId: 'exp2',
                        variant: 'base',
                        range: [
                                from: 80,
                                to:   90
                        ]
                ],
                [
                        experimentId: 'exp1',
                        variant: 'base',
                        range: [
                                from: 90,
                                to:  100
                        ]
                ]

        ]
    }
}
