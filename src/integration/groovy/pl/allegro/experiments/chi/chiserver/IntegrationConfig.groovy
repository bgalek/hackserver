package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.infrastructure.FileBasedExperimentsRepository

@Configuration
class IntegrationConfig {

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

}
