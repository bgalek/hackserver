package pl.allegro.tech.leaders.hackathon.registration.api


import spock.lang.Specification
import spock.lang.Unroll

class TeamSecretSpec extends Specification {

    @Unroll
    def 'should create empty secret for invalid header'() {
        given:
            TeamSecret teamSecret = TeamSecret.fromAuthorizationHeader(headerValue)
        expect:
            teamSecret.getSecret() == null
            !teamSecret.isValid()
        where:
            headerValue << ['', 'Bearer', 'Bearer  ', 'xxxxxx', null]
    }

    def 'should create valid secret for valid header'() {
        given:
            TeamSecret teamSecret = TeamSecret.fromAuthorizationHeader('Bearer secret phrase')
        expect:
            teamSecret.isValid()
            teamSecret.secret == 'secret phrase'
    }
}
