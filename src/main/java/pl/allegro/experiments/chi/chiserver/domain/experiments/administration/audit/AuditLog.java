package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import java.util.List;

public class AuditLog {
    private final String experimentId;
    private final List<CommitDetails> changes;

    AuditLog(String experimentId, List<CommitDetails> changes) {
        this.experimentId = experimentId;
        this.changes = changes;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public List<CommitDetails> getChanges() {
        return changes;
    }
}
