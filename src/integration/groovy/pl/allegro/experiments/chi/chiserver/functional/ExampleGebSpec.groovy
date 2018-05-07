package pl.allegro.experiments.chi.chiserver.functional

import geb.Page

class IndexPage extends Page {
    static at = { title == "Chi Admin" }
}

class ExampleGebSpec extends BaseFunctionalSpec {

    def "should run geb test"() {
        when:
        to IndexPage

        then:
        at IndexPage
    }

}
