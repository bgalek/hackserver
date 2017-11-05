package pl.allegro.experiments.chi.chiserver.interactions

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InteractionBuffer
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.BufferedInteractionRepository
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository
import spock.lang.Specification

import java.time.Instant

class BufferedInteractionRepositorySpec extends Specification {

    InMemoryInteractionRepository repository = new InMemoryInteractionRepository()
    MetricRegistry metrics = new MetricRegistry()
    int bufferMaxSize = 3
    InteractionBuffer buffer = new InteractionBuffer(bufferMaxSize, metrics)
    BufferedInteractionRepository bufferedInteractionRepository = new BufferedInteractionRepository(metrics, buffer, repository)

    def cleanup() {
        buffer.flush()
        repository.interactions.clear()
    }

    def "should save all interactions from buffer when flushing"() {
        given:
        def interaction = sampleInteraction()

        when:
        bufferedInteractionRepository.save(interaction)

        then: "interaction is added to buffer"
        repository.interactions.isEmpty()

        when:
        bufferedInteractionRepository.flush()

        then:
        repository.interactionSaved(interaction)
    }

    def "should drop interactions when buffer is overloaded and saving"() {
        given:
        bufferMaxSize.times { bufferedInteractionRepository.save(sampleInteraction()) }
        def newInteraction = sampleInteraction()

        when:
        bufferedInteractionRepository.save(newInteraction)

        then:
        bufferedInteractionRepository.flush()
        repository.interactions as Set == [newInteraction] as Set
    }

    Interaction sampleInteraction() {
        new Interaction("userId123", "cmid123", "experimentId123",
                "variant134", false, "iphone", Instant.now())
    }
}