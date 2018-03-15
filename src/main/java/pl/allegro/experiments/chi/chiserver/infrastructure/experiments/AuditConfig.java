package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.javers.core.Javers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit.Auditor;

import java.time.ZoneId;

@Configuration
class AuditConfig {

    @Bean
    Auditor auditor(Javers javers) {
        return new Auditor(javers, ZoneId.systemDefault());
    }
}
