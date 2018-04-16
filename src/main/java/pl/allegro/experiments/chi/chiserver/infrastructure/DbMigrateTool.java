package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class DbMigrateTool {
    private static final Logger logger = LoggerFactory.getLogger(DbMigrateTool.class);

    private final ExperimentsRepository experimentsRepository;

    @Autowired
    public DbMigrateTool(ExperimentsRepository experimentsRepository) {
        this.experimentsRepository = experimentsRepository;
    }

    @PostConstruct
    public void action() throws Exception {
       logger.info("running DbMigrateTool");

       experimentsRepository.getExperiment("test-ulubionej-kategorii-na-stronie-gwnej")
       .flatMap(e -> e.getDefinition())
       .ifPresent(e -> {
           if (e.getReportingDefinition().getType() != ReportingType.FRONTEND) {
               logger.info("updating test-ulubionej-kategorii-na-stronie-gwnej ...");

               ReportingDefinition newDef = ReportingDefinition.frontend(
                       Arrays.asList(new EventDefinition(null, "boxView", null, null, "reco__carousel"))
               );

               experimentsRepository.save(e.updateReportingDefinition(newDef));

               logger.info("experiment updated");
           }
       });
    }

}
