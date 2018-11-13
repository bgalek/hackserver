package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.bson.Document
import org.javers.common.exception.JaversException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomParameter
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentGoal
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType

import java.time.ZonedDateTime

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition

class MongoExperimentsRepositoryIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should use cached ExperimentsRepository "(){
      expect:
      experimentsRepository instanceof CachedExperimentsRepository
    }

    def "should save and get an experiment from the repository"() {
        given:
        def from = ZonedDateTime.now().minusDays(1)
        def to =  ZonedDateTime.now().plusDays(1)
        def now = ZonedDateTime.now()
        println "from: "+ trim(from)
        println "to: "+ trim(to)

        def experiment = experimentDefinition()
                .id(UUID.randomUUID().toString())
                .variantNames(['base', 'v1'])
                .percentage(10)
                .groups(['a','b'])
                .activityPeriod(from, to)
                .internalVariantName('v1')
                .fullOnVariantName('base')
                .explicitStatus(ExperimentStatus.FULL_ON)
                .lastExplicitStatusChange(now)
                .description('desc')
                .documentLink('link')
                .author('kazik')
                .reportingDefinition(new ReportingDefinition([new EventDefinition('c','a','v','l','b')], ReportingType.FRONTEND))
                .deviceClass(DeviceClass.phone_android)
                .customParameter(new CustomParameter('k','v'))
                .goal(new ExperimentGoal.Hypothesis("tx_visit", 2),
                      new ExperimentGoal.TestConfiguration(5, 0.05, 0.8, 100_000, 44))
                .build()

        when:
        experimentsRepository.save(experiment)
        def loaded = experimentsRepository.getExperiment(experiment.id).get()

        then: 'loaded object should be equal to the saved object'
        loaded.id == experiment.id
        loaded.variantNames == experiment.variantNames
        loaded.percentage == experiment.percentage
        loaded.groups == experiment.groups
        loaded.activityPeriod == experiment.activityPeriod
        loaded.internalVariantName == experiment.internalVariantName
        loaded.fullOnVariantName == experiment.fullOnVariantName
        loaded.status == experiment.status
        loaded.lastExplicitStatusChange == experiment.lastExplicitStatusChange
        loaded.description == experiment.description
        loaded.documentLink == experiment.documentLink
        loaded.author == experiment.author
        loaded.reportingDefinition == experiment.reportingDefinition
        loaded.deviceClass == experiment.deviceClass
        loaded.customParameter == experiment.customParameter
        loaded.goal.get().hypothesis.leadingMetric == 'tx_visit'
        loaded.goal.get().hypothesis.expectedDiffPercent == 2
        loaded.goal.get().testConfiguration.leadingMetricBaselineValue == 5
        loaded.goal.get().testConfiguration.requiredSampleSize == 100_000
        loaded.goal.get().testConfiguration.testAlpha == 0.05
        loaded.goal.get().testConfiguration.testPower == 0.8

        when:
        Document doc = mongoTemplate.findById(experiment.id, Document, "experimentDefinitions")

        then: 'BSON representation should looks like this'
        doc.getString('_id') ==  experiment.id
        doc.get('variantNames') == ['base', 'v1']
        doc.get('percentage') == 10
        doc.get('groups') == ['a','b']
        doc.get('activityPeriod').getString('activeFrom') == trim(from).toString()
        doc.get('activityPeriod').getString('activeTo') == trim(to).toString()
        doc.getString('internalVariantName') == 'v1'
        doc.getString('fullOnVariantName') == 'base'
        doc.getString('explicitStatus') == 'FULL_ON'
        !doc.get('status')
        doc.getString('description') == 'desc'
        doc.getString('documentLink') == 'link'
        doc.getString('author') == 'kazik'
        doc.get('reportingDefinition').getString('reportingType') == 'FRONTEND'
        with(doc.get('reportingDefinition').get('eventDefinitions')[0]) {
            assert it.getString('category') == 'c'
            assert it.getString('action') == 'a'
            assert it.getString('label') == 'l'
            assert it.getString('value') == 'v'
            assert it.getString('boxName') == 'b'
        }
        doc.getString('deviceClass') =='phone-android'
        doc.get('customParameter').getString('name') =='k'
        doc.get('customParameter').getString('value') =='v'
        with(doc.get('goal').get('hypothesis')) {
            assert it.getString('leadingMetric') == 'tx_visit'
            assert it.getString ('expectedDiffPercent') == '2.00'
        }
        with(doc.get('goal').get('testConfiguration')) {
            assert it.getString ('leadingMetricBaselineValue') == '5.00'
            assert it.get ('requiredSampleSize') == 100_000
            assert it.getString ('testAlpha') == '0.05'
            assert it.getString ('testPower') == '0.80'
            assert it.get ('currentSampleSize') == 44
        }
    }

    def "should remove experiments not tracked by javers"() {
        given:
        def experiment = experimentDefinition()
                .id(UUID.randomUUID().toString())
                .variantNames(['base', 'v1'])
                .percentage(10)
                .build()
        mongoTemplate.save(experiment)

        when:
        experimentsRepository.delete(experiment.id)

        then:
        notThrown JaversException

        and:
        !experimentsRepository.getExperiment(experiment.id).isPresent()
    }

    private ZonedDateTime trim(ZonedDateTime date) {
        date.withNano(0).withFixedOffsetZone()
    }
}
