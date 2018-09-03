package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.bson.Document
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ReportingDefinitionDeserializer
import spock.lang.Specification

class LegacyReportingDefinitionIntegrationSpec extends Specification {


    def "should convert legacy reporting definition document to ReportingDefinition object"() {
        given:
        def reportingDefinitionDeserializer = new ReportingDefinitionDeserializer()

        expect:
        reportingDefinitionDeserializer.convert(document).type == expectedResult.type

        where:
        document                                                                            | expectedResult
        new Document([gtm: false, backendInteractionsEnabled: false, eventDefinitions: []]) | ReportingDefinition.frontend([])
        new Document([gtm: false, backendInteractionsEnabled: true, eventDefinitions: []])  | ReportingDefinition.backend()
        new Document([gtm: true, backendInteractionsEnabled: false, eventDefinitions: []])  | ReportingDefinition.gtm()
    }
}
