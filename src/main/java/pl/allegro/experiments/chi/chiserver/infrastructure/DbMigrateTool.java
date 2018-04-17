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

       String eName = "structure-navigation-electronics";

       experimentsRepository.getExperiment(eName)
       .flatMap(e -> e.getDefinition())
       .ifPresent(e -> {
           if (e.getReportingDefinition().getType() != ReportingType.FRONTEND) {
               logger.info("updating " + eName + "...");

               ReportingDefinition newDef = ReportingDefinition.frontend(
                       Arrays.asList(new EventDefinition(null, "boxView", null, null, "Navigation 2 - above - Kopia od 16.04"),
                                     new EventDefinition(null, "boxView", null, null, "Navigation 2 - below - Kopia od 16.04"),
                                     new EventDefinition(null, "boxView", null, null, "Navigation 1 - above - Kopia od 16.04"),
                                     new EventDefinition(null, "boxView", null, null, "Navigation 1 - below - Kopia od 16.04"),
                                     new EventDefinition(null, "boxView", null, null, "Showcase Rotator"))
               );

               experimentsRepository.save(e.updateReportingDefinition(newDef));

               logger.info("experiment updated");
           }
       });
    }

}
