package pl.allegro.experiments.chi.chiserver.scorer

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.scorer.OfferScorerController
import pl.allegro.experiments.chi.chiserver.utils.ApiActionUtils

class ScorerE2ESpec extends BaseE2EIntegrationSpec implements ApiActionUtils {

    static MAX_RANDOM = 10

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

    def "should keep random scores in <0, 10>"() {
        given:
        postOffers(randomOffers())

        expect:
        fetchScores().every {it -> it.score.value >= 0 && it.score.value <= MAX_RANDOM}
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

    def "should sum default random offer scores with defined offer scores"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        def definedScores = offers.collect {offer -> [
                offer: offer,
                score: [value: 0.5]
        ]}

        when:
        updateScores(definedScores)

        then:
        fetchScores().every {it -> it.score.value >= 0.5}
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
        updateScores(definedScores)

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

    def "should not allow setting score without api token"() {
        when:
        post('api/scorer/scores', [[offer: randomOffers()[0], score: [value: 1]]])

        then:
        thrown(HttpClientErrorException)
    }

    def "should sum scores during update"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        def definedScores = offers.collect {offer -> [
                offer: offer,
                score: [value: 0.5]
        ]}

        when:
        updateScores(definedScores)

        then:
        fetchScores().every {it -> it.score.value >= 0.5 && it.score.value <= MAX_RANDOM + 0.5}

        when:
        updateScores(definedScores)

        then:
        fetchScores().every {it -> it.score.value >= 1 && it.score.value <= MAX_RANDOM + 1}
    }

    def "should reset offer scores when setting new offer set"() {
        given:
        def offers = randomOffers()
        postOffers(offers)

        and:
        updateScores(offers.collect {offer -> [
                offer: offer,
                score: [value: 5]
        ]})

        and:
        fetchScores().every {it -> it.score.value >= 5 && it.score.value <= 5 + MAX_RANDOM}

        when:
        postOffers(offers)

        then:
        fetchScores().every {it -> it.score.value >= 0 && it.score.value <= MAX_RANDOM}
    }

    def "should extend defined offer score set when new offer score occurs during update"() {
        given:
        def offers = randomOffers()
        def offersToScoreAtFirst = offers.subList(0, offers.size() - 1)
        def offerWithoutScore = offers.get(offers.size() - 1)

        and:
        postOffers(offers)

        when:
        updateScores(offersToScoreAtFirst.collect {offer -> [
                offer: offer,
                score: [value: 5]
        ]})

        then:
        fetchScores().find {
            it -> it.offer == offerWithoutScore && it.score.value >= 0 && it.score.value <= MAX_RANDOM
        }

        when:
        updateScores([
            [
                offer: offerWithoutScore,
                score: [value: 5]
            ]
        ])

        then:
        fetchScores().find {it -> it.offer == offerWithoutScore && it.score.value >= 5 && it.score.value <= MAX_RANDOM + 5}
    }

    def "should keep offer score even if it does not appear in update"() {
        given:
        def offers = randomOffers()
        def offersToScoreSecondTime = offers.subList(0, offers.size() - 1)
        def offerWithoutSecondUpdate = offers.get(offers.size() - 1)

        and:
        postOffers(offers)

        and:
        updateScores(offers.collect {offer -> [
                offer: offer,
                score: [value: 5]
        ]})

        when:
        updateScores(offersToScoreSecondTime.collect {offer -> [
                offer: offer,
                score: [value: 5]
        ]})

        then:
        fetchScores().find {it -> it.offer == offerWithoutSecondUpdate && it.score.value >= 5 && it.score.value <= 5 + MAX_RANDOM}
    }

    def updateScores(List newScores) {
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
