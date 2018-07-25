package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository

class CustomParameterSpec extends BaseIntegrationSpec {

    @Autowired
    ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository

    @Autowired
    ExperimentActions experimentActions

    @Autowired
    UserProvider userProvider

    def "should save experiment with custom parameter definition"() {
        given:
            def expId = UUID.randomUUID().toString()

            userProvider.user = new User('Author', [], true)
            def request = [
                    id                  : expId,
                    description         : "desc",
                    variantNames        : ['base'],
                    internalVariantName : 'v1',
                    percentage          : 1,
                    reportingEnabled    : true,
                    customParameterName : 'this is name',
                    customParameterValue: 'this is value'

            ]
            restTemplate.postForEntity(localUrl('/api/admin/experiments/'), request, Map)

            def expectedExperiment = [
                    id              : expId,
                    reportingEnabled: true,
                    status          : 'DRAFT',
                    variants        : [
                            [
                                name        : 'v1',
                                 predicates  : [[type:'INTERNAL']]
                            ],
                            [
                                name        : 'base',
                                predicates  : [
                                        [
                                            type   : 'HASH',
                                            from   : 0,
                                            to     : 1
                                         ],
                                         [
                                            type    : 'CUSTOM_PARAM',
                                            name    : 'this is name',
                                            value   : 'this is value'
                                          ]
                                 ]
                            ]
                    ]
            ]

        when:
            def responseSingle = restTemplate.getForEntity(localUrl("/api/experiments/v1/"), List)

        then:
            responseSingle.body.find({it -> it.id == expId}) == null

        when:
            responseSingle = restTemplate.getForEntity(localUrl("/api/experiments/v2/"), List)

        then:
            responseSingle.body.find({it -> it.id == expId}) == null

        when:
            responseSingle = restTemplate.getForEntity(localUrl("/api/experiments/v3/"), List)

        then:
            responseSingle.body.find({it -> it.id == expId}) == null

        when:
            responseSingle = restTemplate.getForEntity(localUrl("/api/experiments/v4/"), List)

        then:
            responseSingle.body.contains(expectedExperiment)
    }
}
