/*package pl.allegro.experiments.chi.chiserver.ui

import geb.Page
import org.springframework.test.context.ContextConfiguration

class ManageExperimentPage extends Page {

    static url = "#/experiments/test_1"

    boolean successfullyRedirectedToExperimentDetails(String experimentId) {
        waitFor {browser.currentUrl.endsWith(experimentId)}
    }

    void waitForPageLoad() {
        waitFor {$("#experimentIdFormField").displayed}
    }
}*/

/*@ContextConfiguration(classes = [LoggedInAsRootTestConfig])
class ManageExperimentSpec extends BaseUiSpec {

    @UiTest
    def "should redirect to experiment details after creating experiment "() {

        given:
        to ManageExperimentPage

        and:
        waitForPageLoad()

        when:
        on ManageExperimentPage

        then:
        assert $("h1").text() == "No variants. Seriously?"
    }
}*/
