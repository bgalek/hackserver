package pl.allegro.experiments.chi.chiserver.functional

import geb.Page

class CreateExperimentPage extends Page {
    static url = "/experiments/create"
}

class CreateExperimentSpec extends BaseFunctionalSpec {

    def "should create experiment"() {
        given:
        to CreateExperimentPage

        and:
        $("#experimentIdFormField").value(UUID.randomUUID().toString())

        and:
        $("#internalVariantFormField").value(UUID.randomUUID().toString())

        when:
        $("#createExperimentFormSubmitButton").click()

        then:
        true

    }
}