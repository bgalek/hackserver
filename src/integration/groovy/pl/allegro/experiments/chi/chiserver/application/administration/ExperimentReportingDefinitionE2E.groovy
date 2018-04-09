package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import spock.lang.Unroll

class ExperimentReportingDefinitionE2E extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }
    }

    @Unroll
    def "should create #reportingType reported experiment"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id               : 'some2',
                variantNames     : ['v2'],
                percentage       : 10,
                reportingType    : reportingType,
                eventDefinitions: givenEventDefinitions
        ]

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        when:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        responseList.find { it.id == 'some2' }.reportingType == reportingType
        responseList.find { it.id == 'some2' }['eventDefinitions'] == expectedEventDefinitions

        and:
        responseSingle.reportingType == reportingType
        responseSingle['eventDefinitions'] == expectedEventDefinitions

        where:
        reportingType << ['FRONTEND', 'GTM', 'BACKEND']
        expectedEventDefinitions << [
                [
                        [
                                label   : 'label1',
                                category: 'category1',
                                value   : 'value1',
                                action  : 'action1'
                        ],
                        [
                                label   : 'label2',
                                category: 'category2',
                                value   : 'value2',
                                action  : 'action2'
                        ],
                ],
                [
                        [
                                category: 'chiInteraction',
                                action  : 'some2',
                                label   : 'base',
                                value   : ''
                        ],
                        [
                                category: 'chiInteraction',
                                action  : 'some2',
                                label   : 'v2',
                                value   : ''
                        ],
                ],
                null
        ]

        givenEventDefinitions << [
                [
                        [
                                label   : 'label1',
                                category: 'category1',
                                value   : 'value1',
                                action  : 'action1'
                        ],
                        [
                                label   : 'label2',
                                category: 'category2',
                                value   : 'value2',
                                action  : 'action2'
                        ],
                ],
                null,
                null
        ]
    }
}


