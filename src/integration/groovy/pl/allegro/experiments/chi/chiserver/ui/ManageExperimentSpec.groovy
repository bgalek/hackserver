package pl.allegro.experiments.chi.chiserver.ui

import geb.Page
import org.springframework.test.context.ContextConfiguration

class ManageExperimentPage extends Page {

    static url = "#/experiments/create"

    boolean successfullyRedirectedToExperimentDetails(String experimentId) {
        waitFor {browser.currentUrl.endsWith(experimentId)}
    }

    String createExperiment() {
        String experimentId = UUID.randomUUID().toString()
        $("#experimentIdFormField").value(experimentId)
        $("#experimentDescriptionFormField").value(UUID.randomUUID().toString())
        $("#experimentDocumentationLinkFormField").value('https://allegro.pl/' + UUID.randomUUID() + '.pl'.toString())
        $("#experimentAuthorizationGroupsFormField").value(UUID.randomUUID().toString())
        $("#internalVariantFormField").value(UUID.randomUUID().toString())
        $("#createExperimentFormSubmitButton").click()
        experimentId
    }

    void waitForPageLoad() {
        waitFor {$("#experimentIdFormField").displayed}
    }
}

@ContextConfiguration(classes = [LoggedInAsRootTestConfig])
class ManageExperimentSpec extends BaseUiSpec {

    @UiTest
    def "should redirect to experiment details after creating experiment"() {

        given:
        browser.clearCookies()
        to ManageExperimentPage

        and:
        waitForPageLoad()

        when:
        String experimentId = createExperiment()

        then:
        successfullyRedirectedToExperimentDetails(experimentId)
    }
}