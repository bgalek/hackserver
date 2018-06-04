package pl.allegro.experiments.chi.chiserver.application.administration

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.CachedExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.WireMockUtils
import spock.lang.Shared

class CreateExperimentGroupE2ESpec extends BaseIntegrationSpec {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }

        if (!experimentGroupRepository instanceof CachedExperimentGroupRepository) {
            throw new RuntimeException("We should test cached repository")
        }
        WireMockUtils.teachWireMockJson("/experiments", 'some-experiments.json')
    }

    def "should create experiment group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        Map createdGroup = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        createdGroup.experiments == [experimentId1, experimentId2]
        createdGroup.id == groupId
    }

    def "should not create experiment group if contains more than 1 active experiment"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        startExperiment(experimentId1)
        startExperiment(experimentId2)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if contains less than 2 experiments"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        experiments.forEach({e -> createDraftExperiment(e)})

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: experiments
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST

        where:
        experiments << [[], [UUID.randomUUID().toString()]]
    }

    def "should not create experiment group if one of provided experiments does not exist"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if group name is not unique"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        String experimentId3 = UUID.randomUUID().toString()
        String experimentId4 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        createDraftExperiment(experimentId3)
        createDraftExperiment(experimentId4)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId3, experimentId4]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if one of experiments is from stash"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        and:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, 'cmuid_regexp']
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if there is not enough percentage space"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1, 33)
        createDraftExperiment(experimentId2, 34)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if one of experiments is in another group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId1 = UUID.randomUUID().toString()
        String groupId2 = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        String experimentId3 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        createDraftExperiment(experimentId3)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId1,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId2,
                experiments: [experimentId1, experimentId3]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should set group name space to ACTIVE experiment"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        startExperiment(experimentId1)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        Map createdGroup = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        createdGroup.salt == experimentId1
    }
}
