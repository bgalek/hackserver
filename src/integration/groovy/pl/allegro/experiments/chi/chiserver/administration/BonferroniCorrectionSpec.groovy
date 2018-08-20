package pl.allegro.experiments.chi.chiserver.administration

import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics

class BonferroniCorrectionSpec extends BaseE2EIntegrationSpec {

    def "should calculate Bonferroni correction as number of tests"(){
      given:
      def experiment = draftExperiment()

      when:
      classicStatisticsForVariantMetricRepository.save(sampleMetric(experiment.id as String, [variantName: 'group a']))
      classicStatisticsForVariantMetricRepository.save(sampleMetric(experiment.id as String, [variantName: 'group b']))

      then:
      fetchExperiment(experiment.id as String).bonferroniCorrection == 2
    }

    Map DEFAULT_PROPERTIES = [
            durationMillis: 1,
            toDate: "10 Stycznia",
            deviceClass: DeviceClass.phone,
            variantName: "v1",
            metricName: "tx_visit",
            data: null
    ]

    ClassicExperimentStatisticsForVariantMetric sampleMetric(String experimentId, Map customProperties = [:]) {
        def properties = DEFAULT_PROPERTIES + customProperties
        new ClassicExperimentStatisticsForVariantMetric(
                experimentId,
                properties.durationMillis as long,
                properties.toDate as String,
                properties.deviceClass as DeviceClass,
                properties.variantName as String,
                properties.metricName as String,
                properties.data as VariantStatistics)
    }
}
