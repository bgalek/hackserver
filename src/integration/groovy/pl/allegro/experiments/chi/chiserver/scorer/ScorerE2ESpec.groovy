package pl.allegro.experiments.chi.chiserver.scorer

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.scorer.OfferScorerController
import pl.allegro.experiments.chi.chiserver.utils.ApiActionUtils

class ScorerE2ESpec extends BaseE2EIntegrationSpec implements ApiActionUtils {

    def "should return all offer scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        expect:
        fetchScores().size() == offers.size()
    }

    def "should score randomly when scores are not defined explicitly"() {
        given:
        postOffers(randomOffers())

        when:
        def firstScores = fetchScores().collect {it.score.value} as Set
        def secondScores = fetchScores().collect {it.score.value} as Set

        then:
        firstScores != secondScores
    }

    def "should score so each score is <0, 1>"() {
        given:
        postOffers(randomOffers())

        when:
        def scores = fetchScores().collect {it.score.value}

        then:
        scores.every {it <= 1 && it >= 0}
    }

    def "should sort scores by value, from high to low"() {
        given:
        postOffers(randomOffers())

        when:
        def scores = fetchScores().collect {it.score.value}

        then:
        scores.toSorted().reverse() == scores
    }

    def "should not allow setting more than 200 offers"() {
        when:
        postOffers(toManyRandomOffers())

        then:
        thrown(HttpClientErrorException)
    }

    def "should override default random offer scores by defined offer scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        def definedScores = offers.collect {offer -> [
                offer: offer,
                score: [value: 0.5]
        ]}

        when:
        setScores(definedScores)

        then:
        fetchScores().every {it -> it.score.value == 0.5}
    }

    def "should prioritize offer set over defined scores, providing random scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        and:
        def definedScores = randomOffers(120).collect {offer -> [
                offer: offer,
                score: [value: 0.5]
        ]}
        setScores(definedScores)

        when:
        def scores = fetchScores()
        def scoredOffers = scores.collect {offerScore -> offerScore.offer.offerId} as Set

        then:
        offers.collect {offer -> offer.offerId} as Set == scoredOffers

        when:
        postOffers([])

        then:
        fetchScores().size() == 0
    }

    def "should not allow setting score with value out of <0, 1>"() {
        when:
        setScores([[offer: randomOffers()[0], score: [value: value]]])

        then:
        thrown(HttpClientErrorException)

        where:
        value << [-1, 1.1]
    }

    def setScores(List newScores) {
        post('api/scorer/scores', prepareScoresRequest(newScores))
    }

    HttpEntity prepareScoresRequest(List statistics) {
        HttpHeaders headers = new HttpHeaders()
        headers.set("Chi-Token", OfferScorerController.CHI_TOKEN)
        new HttpEntity<>(statistics, headers)
    }

    def fetchScores() {
        get('/api/scorer/scores').body as List
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
