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
import java.util.ArrayList;
import java.util.List;


class ChangeLogProcessor implements ChangeProcessor<List<CommitDetails>> {
    private final ZoneId zoneId;
    private final List<CommitDetails> changes = new ArrayList<>();
    private SimpleTextChangeLog commitChangeLogProcessor;
    private CommitMetadata commitMetadata;

    ChangeLogProcessor(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public void onCommit(CommitMetadata commitMetadata) {
        collectLastCommitDetails();
        this.commitMetadata = commitMetadata;
        this.commitChangeLogProcessor = new SimpleTextChangeLog();
    }

    @Override
    public void onAffectedObject(GlobalId globalId) {
        commitChangeLogProcessor.onAffectedObject(globalId);
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
        commitChangeLogProcessor.onPropertyChange(propertyChange);
    }

    @Override
    public void onValueChange(ValueChange valueChange) {
        commitChangeLogProcessor.onValueChange(valueChange);
    }

    @Override
    public void onReferenceChange(ReferenceChange referenceChange) {
        commitChangeLogProcessor.onReferenceChange(referenceChange);
    }

    @Override
    public void onNewObject(NewObject newObject) {
        commitChangeLogProcessor.onNewObject(newObject);
    }

    @Override
    public void onObjectRemoved(ObjectRemoved objectRemoved) {
        commitChangeLogProcessor.onObjectRemoved(objectRemoved);
    }

    @Override
    public void onContainerChange(ContainerChange containerChange) {
        commitChangeLogProcessor.onContainerChange(containerChange);
    }

    @Override
    public void onSetChange(SetChange setChange) {
        commitChangeLogProcessor.onSetChange(setChange);
    }

    @Override
    public void onArrayChange(ArrayChange arrayChange) {
        commitChangeLogProcessor.onArrayChange(arrayChange);
    }

    @Override
    public void onListChange(ListChange listChange) {
        commitChangeLogProcessor.onListChange(listChange);
    }

    @Override
    public void onMapChange(MapChange mapChange) {
        commitChangeLogProcessor.onMapChange(mapChange);
    }

    @Override
    public List<CommitDetails> result() {
        collectLastCommitDetails();
        return changes;
    }

    private void collectLastCommitDetails() {
        if (commitMetadata != null) {
            CommitDetails commitDetails = createCommitDetails();
            changes.add(commitDetails);
        }
    }

    private CommitDetails createCommitDetails() {
        String author = commitMetadata.getAuthor();
        ZonedDateTime date = toZonedDateTime(commitMetadata.getCommitDate());
        String changelog = commitChangeLogProcessor.result();
        return new CommitDetails(author, date, changelog);
    }

    private ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        final ZoneOffset offset = zoneId.getRules().getOffset(localDateTime);
        return ZonedDateTime.ofLocal(localDateTime, offset, offset);
    }
}
