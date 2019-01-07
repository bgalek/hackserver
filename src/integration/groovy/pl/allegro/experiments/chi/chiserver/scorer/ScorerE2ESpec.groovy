package pl.allegro.experiments.chi.chiserver.scorer

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.scorer.OfferScorerController
import pl.allegro.experiments.chi.chiserver.utils.ApiActionUtils

class ScorerE2ESpec extends BaseE2EIntegrationSpec implements ApiActionUtils {

    static DEFAULT_PARAMETER_VALUE = 1

    def "should return all offer parameters"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        expect:
        fetchParameters().size() == offers.size()
    }

    def "should provide default parameters when parameters are not defined explicitly"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        expect:
        offers.collect {it -> it.offerId} as Set == fetchParameters().collect {it.offer.offerId} as Set
    }

    def "should keep default parameter value equal DEFAULT_PARAMETER_VALUE"() {
        given:
        postOffers(randomOffers())

        when:
        def parametersAlpha = fetchParameters().collect {it.parameters.alpha} as Set
        def parametersBeta = fetchParameters().collect {it.parameters.beta} as Set

        then:
        parametersAlpha == [DEFAULT_PARAMETER_VALUE] as Set
        parametersBeta == [DEFAULT_PARAMETER_VALUE] as Set
    }

    def "should not allow setting more than 200 offers"() {
        when:
        postOffers(toManyRandomOffers())

        then:
        thrown(HttpClientErrorException)
    }

    def "should override default offer parameters with explicitly defined offer parameters"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        def definedParameters = offers.collect {offer -> [
                offer: offer,
                parameters: [
                        alpha: 80,
                        beta: 100
                ]
        ]}

        when:
        updateParameters(definedParameters)

        then:
        fetchParameters().every { it -> it.parameters.alpha == 80 && it.parameters.beta == 100}
    }

    def "should prioritize offer set over defined offer parameters, providing default parameters"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        and:
        def definedParameters = randomOffers(120).collect {offer -> [
                offer: offer,
                parameters: [
                        alpha: 80,
                        beta: 100
                ]
        ]}
        updateParameters(definedParameters)

        when:
        def parameters = fetchParameters()
        def parametrizedOffers = parameters.collect {it -> it.offer.offerId} as Set

        then:
        offers.collect {offer -> offer.offerId} as Set == parametrizedOffers

        when:
        postOffers([])

        then:
        fetchParameters().size() == 0
    }

    def "should not allow setting parameters without api token"() {
        when:
        post('api/scorer/parameters', [[offer: randomOffers()[0], parameters: [
                alpha: 80,
                beta: 100
        ]]])

        then:
        thrown(HttpClientErrorException)
    }

    def "should clear old parameters when setting new"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        and:
        def definedParameters = offers.collect {offer -> [
                offer: offer,
                parameters: [
                        alpha: 80,
                        beta: 100
                ]
        ]}

        and:
        updateParameters(definedParameters)

        when:
        updateParameters(offers.collect {offer -> [
                offer: offer,
                parameters: [
                        alpha: 90,
                        beta: 900
                ]
        ]})

        then:
        fetchParameters().every {it -> it.parameters.alpha == 90 && it.parameters.beta == 900}

        when:
        updateParameters([])

        then:
        fetchParameters().every {it -> it.parameters.alpha == DEFAULT_PARAMETER_VALUE && it.parameters.beta == DEFAULT_PARAMETER_VALUE}
    }

    def "should return empty list from deprecated /scores endpoint"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        when:
        def scores = get('/api/scorer/scores').body as List

        then:
        scores == []
    }

    def updateParameters(List newParameters) {
        post('api/scorer/parameters', prepareParametersRequest(newParameters))
    }

    HttpEntity prepareParametersRequest(List statistics) {
        HttpHeaders headers = new HttpHeaders()
        headers.set("Chi-Token", OfferScorerController.CHI_TOKEN)
        new HttpEntity<>(statistics, headers)
    }

    def fetchParameters() {
        get('/api/scorer/parameters').body as List
    }

    def postOffers(List offers) {
        post('api/scorer/offers', offers)
    }

    def randomOffers() {
        randomOffers(99)
    }

    def randomOffers(size) {
        def offerIds = (1..size).collect {randomOfferId()}
        offerIds.collect {it -> [offerId: it]}
    }

    def toManyRandomOffers() {
        def offerIds = (1..201).collect {randomOfferId()}
        offerIds.collect {it -> [offerId: it]}
    }

    def randomOfferId() {
        UUID.randomUUID().toString()
    }
}
