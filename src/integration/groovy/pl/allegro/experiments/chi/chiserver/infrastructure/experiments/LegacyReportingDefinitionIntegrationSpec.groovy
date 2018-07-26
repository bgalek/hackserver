package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ReportingDefinitionDeserializer

class LegacyReportingDefinitionIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ReportingDefinitionDeserializer reportingDefinitionDeserializer

    def "should convert legacy reporting definition document to ReportingDefinition object"() {
        expect:
        reportingDefinitionDeserializer.convert(document).type == expectedResult.type

        where:
        document << [
                new Document([gtm: false, backendInteractionsEnabled: false, eventDefinitions: []]),
                new Document([gtm: false, backendInteractionsEnabled: true, eventDefinitions: []]),
                new Document([gtm: true, backendInteractionsEnabled: false, eventDefinitions: []])
        ]
        expectedResult << [
                ReportingDefinition.frontend([]),
                ReportingDefinition.backend(),
                ReportingDefinition.gtm()
        ]
    }
}
