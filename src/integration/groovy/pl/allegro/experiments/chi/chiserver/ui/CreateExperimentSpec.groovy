package pl.allegro.experiments.chi.chiserver.ui

import geb.Page
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

class CreateExperimentPage extends Page {

    static url = "#/experiments/create"

    boolean successfullyRedirectedToExperimentDetails(String experimentId) {
        waitFor {browser.currentUrl.endsWith(experimentId)}
    }

    String createExperiment() {
        String experimentId = UUID.randomUUID().toString()
        $("#experimentIdFormField").value(experimentId)
        $("#internalVariantFormField").value(UUID.randomUUID().toString())
        $("#createExperimentFormSubmitButton").click()
        experimentId
    }

    void waitForPageLoad() {
        waitFor {$("#experimentIdFormField").displayed}
    }
}

@ContextConfiguration(classes = [LoggedInAsRootTestConfig])
@Ignore
class CreateExperimentSpec extends BaseUiSpec {

    def "should redirect to experiment details after creating experiment"() {
        given:
        to CreateExperimentPage

        and:
        waitForPageLoad()

        when:
        String experimentId = createExperiment()

        then:
        successfullyRedirectedToExperimentDetails(experimentId)
    }
}