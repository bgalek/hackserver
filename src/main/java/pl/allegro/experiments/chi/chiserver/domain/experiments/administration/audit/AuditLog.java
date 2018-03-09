package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.diff.Change;

import java.util.List;

public class AuditLog {
    private final String experimentId;
    private final List<Change> changes;

    AuditLog(String experimentId, List<Change> changes) {
        this.experimentId = experimentId;
        this.changes = changes;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public List<Change> getChanges() {
        return changes;
    }
}
