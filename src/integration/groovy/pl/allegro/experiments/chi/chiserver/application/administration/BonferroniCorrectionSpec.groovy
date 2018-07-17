package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository

class BonferroniCorrectionSpec extends BaseIntegrationSpec {

    @Autowired
    ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository

    @Autowired
    ExperimentActions experimentActions

    @Autowired
    UserProvider userProvider

    def "should calculate Bonferroni correction as number of tests"(){
      given:
      def expId =  UUID.randomUUID().toString()

      userProvider.user = new User('Author', [], true)
      def request = [
              id                 : expId,
              description        : "desc",
              variantNames       : [],
              internalVariantName: 'v1',
              percentage         : 1,
              groups             : ['group a', 'group b'],
              reportingEnabled   : true
      ]
      restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

      classicStatisticsForVariantMetricRepository.save(createSample(expId, 'group a'))
      classicStatisticsForVariantMetricRepository.save(createSample(expId, 'group b'))

      when:
      def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/$expId/"), Map)

      then:
      responseSingle.body.bonferroniCorrection == 2
    }

    ClassicExperimentStatisticsForVariantMetric createSample(String expId, String variantName) {
        new ClassicExperimentStatisticsForVariantMetric(expId, 1, "10 Stycznia", DeviceClass.phone, variantName, "tx_visit", null )
    }
}
