package pl.allegro.experiments.chi.chiserver.commands

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCommandException
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository

import java.time.LocalDate

class DeleteExperimentCommandIntegrationSpec extends BaseCommandIntegrationSpec {

    @Autowired
    ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository

    def "should delete experiment"() {
        given:
        def experiment = draftExperiment()

        when:
        deleteExperiment(experiment.id)

        then:
        !experimentsExists(experiment.id)
    }

    def "should not delete experiment with statistics"() {
        given:
        def experiment = draftExperiment()

        and:
        def metric = sampleClassicExperimentStatisticsForVariantMetric(experiment)
        classicStatisticsForVariantMetricRepository.save(metric)

        when:
        deleteExperiment(experiment.id)

        then:
        def exception = thrown ExperimentCommandException
        exception.message == "Experiment with statistics cannot be deleted: $experiment.id"

        and:
        experimentsExists(experiment.id)
    }



    static ClassicExperimentStatisticsForVariantMetric sampleClassicExperimentStatisticsForVariantMetric(ExperimentDefinition experiment) {
        new ClassicExperimentStatisticsForVariantMetric(
                experiment.id,
                1,
                LocalDate.now().toString(),
                experiment.deviceClass.orElse(DeviceClass.all),
                experiment.variantNames.first(),
                "tx_visit",
                null)
    }
}