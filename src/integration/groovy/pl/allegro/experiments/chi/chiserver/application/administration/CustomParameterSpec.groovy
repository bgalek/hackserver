package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository
import spock.lang.Unroll

class CustomParameterSpec extends BaseIntegrationSpec {

    @Autowired
    ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository

    @Autowired
    ExperimentActions experimentActions

    @Autowired
    UserProvider userProvider

    @Unroll
    def "should save experiment with custom parameter definition and return it for clients with API version >= 4"() {
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

        when:
            def response = restTemplate.getForEntity(localUrl("/api/experiments/" + version), List)

        then:
            condtion(response.body.find({it.id == expId}))

        where:
        version | condtion
        "v1/"   | {!it}
        "v2/"   | {!it}
        "v3/"   | {!it}
        "v4/"   | hasBaseVariantWithCustomParamPredicate('this is name', 'this is value')
        ""      | hasBaseVariantWithCustomParamPredicate('this is name', 'this is value')
    }

    def hasBaseVariantWithCustomParamPredicate(String expectedName, String expectedValue) {
        return {
            it.variants
                    .find({ it.name == 'base' })
                    .predicates
                    .contains([type: 'CUSTOM_PARAM', name: expectedName, value: expectedValue])
        }
    }

    def hasBaseVariantWithoutCustomParamPredicate() {
        return {
            !it.variants
                    .find({ it.name == 'base' })
                    .predicates
                    .find({it.type == 'CUSTOM_PARAM'})
        }
    }

    @Unroll
    def "should not save experiment with partially defined custom parameter"() {
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
                    customParameterName : name,
                    customParameterValue: 'value'
            ]

        when:
            restTemplate.postForEntity(localUrl('/api/admin/experiments/'), request, Map)

        then:
            HttpClientErrorException exception = thrown()
            exception.statusCode == HttpStatus.BAD_REQUEST

        where:
            name    | _
            null    | _
            ''      | _
            ' '     | _
    }

    @Unroll
    def 'should ignore blank custom parameter fields and trim not blank fields'() {
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
                    customParameterName : name,
                    customParameterValue: value
            ]
            restTemplate.postForEntity(localUrl('/api/admin/experiments/'), request, Map)

        when:
            def response = restTemplate.getForEntity(localUrl("/api/experiments/"), List)

        then:
            condition(response.body.find({it.id == expId}))

        where:
            name            | value                     | condition
            null            | null                      | hasBaseVariantWithoutCustomParamPredicate()
            ''              | null                      | hasBaseVariantWithoutCustomParamPredicate()
            ' '             | null                      | hasBaseVariantWithoutCustomParamPredicate()
            ' notBlank '    | ' this will be trimmed '  | hasBaseVariantWithCustomParamPredicate('notBlank', 'this will be trimmed')


    }
}
