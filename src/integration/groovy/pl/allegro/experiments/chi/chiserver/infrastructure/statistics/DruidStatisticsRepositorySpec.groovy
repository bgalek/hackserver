package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.infrastructure.ClasspathContentLoader

import java.time.LocalDate

class DruidStatisticsRepositorySpec extends BaseIntegrationSpec {
    @Autowired
    DruidStatisticsRepository druidStatisticsRepository

    def "should parse simple tx_visit statistics"() {
        given:
        def druidResponse = ClasspathContentLoader.localResource('druid-stats-list.json')

        when:
        def latest = druidStatisticsRepository.parseStatistics(druidResponse, "someExp", LocalDate.now(), "all")

        then:
        latest.metrics.size() == 1
        latest.metrics['tx_visit'].size() == 2

        assertStatistics latest.metrics["tx_visit"]["base"], [
                count: 1844715, value: 0.05505457520484924, diff: 0.0, pValue: 1.0]

        assertStatistics latest.metrics["tx_visit"]["metrum"], [
                count: 1859598, value: 0.05314482003450394, diff: -0.00190975540317595, pValue: 4.440892098500626E-16]
    }

    void assertStatistics(actual, expected) {
        assert actual.count == expected.count
        assert actual.value == expected.value
        assert actual.diff == expected.diff
        assert actual.pValue == expected.pValue
    }
}
