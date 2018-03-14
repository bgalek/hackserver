package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.jetbrains.annotations.NotNull;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Audit {
    private final Javers javers;

    public Audit(Javers javers) {
        this.javers = javers;
    }

    public AuditLog getAuditLog(String experimentId) {
        final JqlQuery query = QueryBuilder
                .byInstanceId(experimentId, Experiment.class)
                .withNewObjectChanges()
                .build();
        final List<Change> changes = javers.findChanges(query);
        final List<CommitDetails> commitChanges = groupByCommitId(changes);
        return new AuditLog(experimentId, commitChanges);
    }

    private List<CommitDetails> groupByCommitId(List<Change> changes) {
        final Map<CommitMetadata, List<Change>> groupedChanges = changes.stream()
                .collect(Collectors.groupingBy(this::commitMetadata));
        final List<CommitDetails> commitedChanges = groupedChanges.entrySet().stream()
                .map(this::createCommitChanges)
                .sorted(Comparator.comparing(CommitDetails::getDate).reversed())
                .collect(Collectors.toList());
        return commitedChanges;
    }

    @NotNull
    private CommitDetails createCommitChanges(Entry<CommitMetadata, List<Change>> entry) {
        final CommitMetadata commitMetadata = entry.getKey();
        final List<Change> changes = entry.getValue();
        String changeLog = javers.processChangeList(changes, new TextChangeLogWithoutCommitMetadata());
        return new CommitDetails(
                commitMetadata.getAuthor(),
                commitMetadata.getCommitDate(),
                changeLog);
    }

    private CommitMetadata commitMetadata(Change change) {
        return change.getCommitMetadata()
                .orElseThrow(() -> new IllegalStateException("Missing commit metadata"));
    }
}
