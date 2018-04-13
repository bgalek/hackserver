package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.time.ZoneId;
import java.util.List;

public class Auditor {
    private final Javers javers;
    private final ZoneId zoneId;

    public Auditor(Javers javers, ZoneId zoneId) {
        this.javers = javers;
        this.zoneId = zoneId;
    }

    public AuditLog getAuditLog(String experimentId) {
        final JqlQuery query = QueryBuilder
                .byInstanceId(experimentId, ExperimentDefinition.class)
                .withChildValueObjects()
                .withNewObjectChanges()
                .build();
        final List<Change> allChanges = javers.findChanges(query);
        final List<CommitDetails> changes = javers.processChangeList(allChanges, new ChangeLogProcessor(zoneId));
        return new AuditLog(experimentId, changes);
    }
}
