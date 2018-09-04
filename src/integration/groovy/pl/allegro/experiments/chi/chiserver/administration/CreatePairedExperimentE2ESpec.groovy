package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec

import static pl.allegro.experiments.chi.chiserver.utils.SampleExperimentRequests.sampleExperimentCreationRequestProperties

@Deprecated
class CreatePairedExperimentE2ESpec extends BaseE2EIntegrationSpec {

    def "should create paired experiment"() {
        given:
        def groupId = UUID.randomUUID().toString()
        def firstExperiment = draftExperiment()
        def secondExperiment = pairedExperiment([firstExperiment.id], groupId)

        when:
        def group = fetchExperimentGroup(groupId)

        then:
        group.id == groupId
        group.experiments == [firstExperiment.id, secondExperiment.id]
    }

    def "should not create paired experiment if cannot create group"() {
        given:
        def groupId = UUID.randomUUID().toString()
        def experimentId = UUID.randomUUID().toString()
        def request = sampleExperimentCreationRequestProperties([id: experimentId])

        when:
        createPairedExperiment(request, ["non existent id"])

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        when:
        fetchExperiment(experimentId)

        then:
        exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND

        when:
        fetchExperimentGroup(groupId)

        then:
        exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND
    }

    def "should not create paired experiment if cannot create experiment"() {
        given:
        def experiment = draftExperiment()
        def nonExistentId = UUID.randomUUID().toString()
        def groupId = UUID.randomUUID().toString()
        def request = sampleExperimentCreationRequestProperties([id: experiment.id])

        when:
        createPairedExperiment(request, [nonExistentId], groupId)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.BAD_REQUEST

        when:
        fetchExperiment(nonExistentId)

        then:
        exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND

        when:
        fetchExperimentGroup(groupId)

        then:
        exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND
    }
}
