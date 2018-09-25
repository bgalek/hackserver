package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class DbMigrateTool {
    private static final Logger logger = LoggerFactory.getLogger(DbMigrateTool.class);

    private final ExperimentsRepository experimentsRepository;
    private final ExperimentGroupRepository experimentGroupRepository;

    @Autowired
    public DbMigrateTool(ExperimentsRepository experimentsRepository, ExperimentGroupRepository experimentGroupRepository) {
        this.experimentsRepository = experimentsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    @PostConstruct
    public void action() throws Exception {
        logger.info("running DbMigrateTool ...");

        persistAllocationTables();
    }

    private void persistAllocationTables() {
        for (ExperimentGroup group : experimentGroupRepository.findAll()) {
            if (!group.getAllocationTable().isEmpty()) continue;

            logger.info("persisting group " + group.getId() );
        }
    }

}
