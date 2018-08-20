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

    @UiTest
    def "should show validation statements after create experiment without required fields"() {

        given:
        to CreateExperimentPage

        and:
        waitForPageLoad()

        when:
        pushTheCreateButton()

        then:
        $("div:nth-child(1) > div.flex.xs11 > div > div.input-group__details > div").text() == "Experiment ID is required"

        and:
        $("div:nth-child(3) > div > div > div.input-group__details > div").text() == "No variants. Seriously?"
    }

    @UiTest
    def "should check if all fields exist"() {

        given:
        to CreateExperimentPage

        and:
        waitForPageLoad()

        expect:
        title == "Chi Admin"

        and:
        $("#experimentIdFormField").displayed

        and:
        $("#experimentIdSlug").displayed

        and:
        $("#experimentDescriptionFormField").displayed

        and:
        $("#experimentDocumentationLinkFormField").displayed

        and:
        $("#experimentAuthorizationGroupsFormField").displayed

        and:
        $("#deviceClassDropdown").displayed

        and:
        $("#percentageVariantSlider").displayed

        and:
        $("#experimentVariants").displayed

        and:
        $("#internalVariantFormField").displayed

        and:
        $("#internalVariantNameSlug").displayed

        and:
        $("#reportingTypeDropdown").displayed

        and:
        $("#createExperimentFormSubmitButton").displayed
    }
}
