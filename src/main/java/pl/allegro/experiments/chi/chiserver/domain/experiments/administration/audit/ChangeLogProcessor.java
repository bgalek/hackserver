package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


class ChangeLogProcessor implements ChangeProcessor<List<CommitDetails>> {
    private final ZoneId zoneId;
    private final LinkedList<CommitChangeLogProcessor> commitChangeLogProcessors = new LinkedList<>();

    ChangeLogProcessor(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        commitChangeLogProcessors.add(new CommitChangeLogProcessor(commitMetadata));
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        getCommitChangeLogProcessor().onAffectedObject(globalId);
    }

    @Override
    public void beforeChangeList() {

    }

    @Override
    public void afterChangeList() {

    }

    @Override
    public void beforeChange(Change change) {

    }

    @Override
    public void afterChange(Change change) {

    }

    @Override
    public void onPropertyChange(PropertyChange propertyChange) {
        getCommitChangeLogProcessor().onPropertyChange(propertyChange);
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
        getCommitChangeLogProcessor().onValueChange(valueChange);
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
        getCommitChangeLogProcessor().onReferenceChange(referenceChange);
    }

    @Override
    public void onNewObject(NewObject newObject) {
        getCommitChangeLogProcessor().onNewObject(newObject);
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        getCommitChangeLogProcessor().onObjectRemoved(objectRemoved);
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {
        getCommitChangeLogProcessor().onContainerChange(containerChange);
    }

    @Override
    public void onSetChange(SetChange setChange) {
        getCommitChangeLogProcessor().onSetChange(setChange);
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
        getCommitChangeLogProcessor().onArrayChange(arrayChange);
    }

    @Override
    public void onListChange(ListChange listChange) {
        getCommitChangeLogProcessor().onListChange(listChange);
    }

    @Override
    public void onMapChange(MapChange mapChange) {
        getCommitChangeLogProcessor().onMapChange(mapChange);
    }

    private SimpleTextChangeLog getCommitChangeLogProcessor() {
        return commitChangeLogProcessors.getLast().getChangeLogProcessor();
    }

    @Override
    public List<CommitDetails> result() {
        return commitChangeLogProcessors.stream()
                .map(this::toCommitDetails)
                .collect(Collectors.toList());
    }

    private CommitDetails toCommitDetails(CommitChangeLogProcessor commitChangeLogProcessor) {
        return commitChangeLogProcessor.toCommitDetails(zoneId);
    }

    private static class CommitChangeLogProcessor {
        final SimpleTextChangeLog changeLogProcessor = new SimpleTextChangeLog();
        final CommitMetadata commitMetadata;

        private CommitChangeLogProcessor(CommitMetadata commitMetadata) {
            this.commitMetadata = commitMetadata;
        }

        SimpleTextChangeLog getChangeLogProcessor() {
            return changeLogProcessor;
        }

        CommitDetails toCommitDetails(ZoneId zoneId) {
            String author = commitMetadata.getAuthor();
            ZonedDateTime date = toZonedDateTime(commitMetadata.getCommitDate(), zoneId);
            String changelog = changeLogProcessor.result();
            return new CommitDetails(author, date, changelog);
        }

        private ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
            final ZoneOffset offset = zoneId.getRules().getOffset(localDateTime);
            return ZonedDateTime.ofLocal(localDateTime, offset, offset);
        }
    }
}
