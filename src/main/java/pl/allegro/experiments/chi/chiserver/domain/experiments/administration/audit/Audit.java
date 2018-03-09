package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.util.List;

public class Audit {

    final Javers javers;

    public Audit(Javers javers) {
        this.javers = javers;
    }

    public AuditLog getAuditLog(String experimentId) {
        final JqlQuery query = QueryBuilder.byInstanceId(experimentId, Experiment.class).build();
        final List<Change> changes = javers.findChanges(query);
        return new AuditLog(experimentId, changes);
    }
}
