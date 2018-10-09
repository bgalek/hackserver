package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import javax.annotation.PostConstruct;

@Service
public class DbMigrateTool {
    private static final Logger logger = LoggerFactory.getLogger(DbMigrateTool.class);

    private final ExperimentsRepository experimentsRepository;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ClientExperimentFactory clientExperimentFactory;

    @Autowired
    public DbMigrateTool(ExperimentsRepository experimentsRepository, ExperimentGroupRepository experimentGroupRepository, ClientExperimentFactory clientExperimentFactory) {
        this.experimentsRepository = experimentsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
        this.clientExperimentFactory = clientExperimentFactory;
    }

    @PostConstruct
    public void action() throws Exception {
        logger.info("running DbMigrateTool ...");

        persistAllocationTables();
    }

    private void persistAllocationTables() {
        experimentGroupRepository.findAll().stream()
                .filter(g -> g.getAllocationTable().isEmpty())
                .forEach(g ->  clientExperimentFactory.persistAllocationForLegacyGroup(g));
    }

}