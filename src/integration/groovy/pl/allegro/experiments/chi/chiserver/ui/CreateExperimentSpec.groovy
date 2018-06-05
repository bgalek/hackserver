package pl.allegro.experiments.chi.chiserver.ui

import geb.Page
import org.springframework.test.context.ContextConfiguration

class CreateExperimentPage extends Page {

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

    String pushTheCreateButton(){
        $("#createExperimentFormSubmitButton").click()
    }
    void waitForPageLoad() {
        waitFor {$("#experimentIdFormField").displayed}
    }
}

@ContextConfiguration(classes = [LoggedInAsRootTestConfig])
class CreateExperimentSpec extends BaseUiSpec {

    @UiTest
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
    def "should show validation statements after create experiment without required fields"() {

        given:
        to CreateExperimentPage

        and:
        waitForPageLoad()

        when:
        pushTheCreateButton()

        then:
        assert $("div:nth-child(1) > div.flex.xs11 > div > div.input-group__details > div").text() == "Experiment ID is required"

        and:
        assert $("div:nth-child(3) > div > div > div.input-group__details > div").text() == "No variants. Seriously?"
    }
    def "should check that all fields are exist"() {

        given:
        to CreateExperimentPage

        and:
        waitForPageLoad()

        when:
        assert title == "Chi Admin"

        then:
        assert $("#experimentIdFormField").displayed

        and:
        assert $("#experimentIdSlug").displayed

        and:
        assert $("#experimentDescriptionFormField").displayed

        and:
        assert $("#experimentDocumentationLinkFormField").displayed

        and:
        assert $("#experimentAuthorizationGroupsFormField").displayed

        and:
        assert $("#deviceClassDropdown").displayed

        and:
        assert $("#percentageVariantSlider").displayed

        and:
        assert $("#experimentVariants").displayed

        and:
        assert $("#internalVariantFormField").displayed

        and:
        assert $("#internalVariantNameSlug").displayed

        and:
        assert $("#reportingTypeDropdown").displayed

        and:
        assert $("#createExperimentFormSubmitButton").displayed
    }
}
