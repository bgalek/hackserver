package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Auditor {
    private final Javers javers;
    private final ZoneId zoneId;

    public Auditor(Javers javers, ZoneId zoneId) {
        this.javers = javers;
        this.zoneId = zoneId;
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
        return groupedChanges.entrySet().stream()
                .map(this::createCommitChanges)
                .sorted(Comparator.comparing(CommitDetails::getDate).reversed())
                .collect(Collectors.toList());
    }

    private CommitMetadata commitMetadata(Change change) {
        return change.getCommitMetadata()
                .orElseThrow(() -> new IllegalStateException("Missing commit metadata"));
    }

    private CommitDetails createCommitChanges(Entry<CommitMetadata, List<Change>> entry) {
        final CommitMetadata commitMetadata = entry.getKey();
        final List<Change> changes = entry.getValue();
        String changeLog = javers.processChangeList(changes, new TextChangeLogWithoutCommitMetadata());
        final ZonedDateTime commitDate = toZonedDateTime(commitMetadata.getCommitDate());
        return new CommitDetails(
                commitMetadata.getAuthor(),
                commitDate,
                changeLog);
    }

    private ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        final ZoneOffset offset = zoneId.getRules().getOffset(localDateTime);
        return ZonedDateTime.ofLocal(localDateTime, offset, offset);
    }

}
