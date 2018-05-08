package pl.allegro.experiments.chi.chiserver.functional

import geb.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig

class CreateExperimentPage extends Page {

    static url = "#/experiments/create"

    static content = {
        createExperiment {
            String experimentId = UUID.randomUUID().toString()
            $("#experimentIdFormField").value(experimentId)
            $("#internalVariantFormField").value(UUID.randomUUID().toString())
            $("#createExperimentFormSubmitButton").click()
            experimentId
        }

        successfullyRedirectedToExperimentDetails (wait: true) {
            localUrl, experimentId ->
                browser.currentUrl == localUrl("/#/experiments/$experimentId")
        }
    }
}

@ContextConfiguration(classes = [ExperimentsTestConfig])
class CreateExperimentSpec extends BaseFunctionalSpec {

    @Autowired
    UserProvider userProvider

    def "should redirect to experiment details after creating experiment"() {
        given:
        to CreateExperimentPage

        and:
        userProvider.user = new User('Author', [], true)

        when:
        String experimentId = createExperiment

        then:
        successfullyRedirectedToExperimentDetails({ localUrl(it) }, experimentId)

    }
}