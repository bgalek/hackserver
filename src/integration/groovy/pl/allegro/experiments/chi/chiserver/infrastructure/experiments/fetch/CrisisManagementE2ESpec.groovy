package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.test.context.TestPropertySource
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

@TestPropertySource(properties = [
        "chi.crisisManagement.enabled: true",
        "chi.crisisManagement.whitelist: whitelisted_experiment_id"
])
class CrisisManagementE2ESpec extends BaseE2EIntegrationSpec{

    def "should return only experiments listed on whitelist"() {
        given:
        def whitelistedExperiment = startedExperiment([id: 'whitelisted_experiment_id'])

        and:
        startedExperiment()
        startedExperiment()
        startedExperiment()

        expect:
        fetchClientExperiments().collect {it.id} == [whitelistedExperiment.id]
    }
}
