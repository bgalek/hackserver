package pl.allegro.experiments.chi.chiserver.scorer

import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.utils.ApiActionUtils

class ScorerE2ESpec extends BaseE2EIntegrationSpec implements ApiActionUtils {

    def "should return all offer scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        expect:
        fetchScores().size() == offers.size()
    }

    def "should score randomly"() {
        given:
        postOffers(randomOffers())

        when:
        def firstScores = fetchScores().collect {it.score.value} as Set
        def secondScores = fetchScores().collect {it.score.value} as Set

        then:
        firstScores != secondScores
    }

    def "should score so score sum is always 1"() {
        given:
        postOffers(randomOffers())

        when:
        def scores = fetchScores().collect {it.score.value}

        then:
        Math.abs(1 - scores.sum()) < 0.0001
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

    def "should update offer scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        def newScores = offers.collect {offer -> [
                offer: offer,
                score: [value: 0.5]
        ]}

        when:
        updateScores(newScores)

        then:
        fetchScores().every {it -> it.score.value == 0.5}
    }

    def updateScores(List newScores) {
        post('api/scorer/scores', newScores)
    }

    def fetchScores() {
        get('/api/scorer/scores').body as List
    }

    def postOffers(List offers) {
        post('api/scorer/offers', offers)
    }

    def randomOffers() {
        def offerIds = (1..99).collect {randomOfferId()}
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
