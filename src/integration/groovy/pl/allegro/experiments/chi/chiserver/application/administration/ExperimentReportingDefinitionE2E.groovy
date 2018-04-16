package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
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
    def "should preserve reporting definition during #reportingType experiment lifecycle"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : experimentId,
                variantNames    : ['v2'],
                percentage      : 10,
                reportingType   : reportingType,
                eventDefinitions: givenEventDefinitions
        ]

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        and:
        def startRequest = [
                experimentDurationDays: 30
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/start"), startRequest, Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/update-descriptions"),
                [
                        description : 'chi rulez',
                        documentLink: 'new link',
                        groups      : ['group c'],
                ], Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/update-variants"),
                [
                        percentage         : 18,
                        variantNames       : ['v2'],
                        internalVariantName: 'internV',
                        deviceClass        : 'phone'
                ], Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/pause"), Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/resume"), Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/prolong"), [experimentAdditionalDays: 30], Map)

        and:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/stop"), Map)

        when:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List).body
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map).body

        then:
        responseList.find { it['id'] == experimentId }['reportingType'] == expectedReportingType
        responseList.find { it['id'] == experimentId }['eventDefinitions'] as Set == expectedEventDefinitions as Set

        and:
        responseSingle['reportingType'] == expectedReportingType
        responseSingle['eventDefinitions'] as Set == expectedEventDefinitions as Set

        where:
        experimentId << ['e5', 'e6', 'e7', 'e8']
        reportingType << ['FRONTEND', 'GTM', 'BACKEND', null]
        expectedReportingType << ['FRONTEND', 'GTM', 'BACKEND', 'BACKEND']
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
                        ]
                ],
                [
                        [
                                category: 'chiInteraction',
                                action  : experimentId,
                                label   : 'v2',
                                value   : ''
                        ]
                ],
                [],
                []
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
                null,
                null
        ]
    }
}


