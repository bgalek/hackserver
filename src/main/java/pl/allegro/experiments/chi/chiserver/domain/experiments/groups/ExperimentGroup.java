package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.data.annotation.PersistenceConstructor;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.reverse;
import static pl.allegro.experiments.chi.chiserver.util.Lists.join;

public class ExperimentGroup implements Comparable<ExperimentGroup> {
    private final String id;
    private final String salt;
    private final List<String> experiments;
    private final AllocationTable allocationTable;

    @PersistenceConstructor
    ExperimentGroup(String id, String salt, List<String> experiments, AllocationTable allocationTable) {
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(salt != null);
        Preconditions.checkArgument(experiments != null);
        this.id = id;
        this.salt = salt;
        this.experiments = ImmutableList.copyOf(experiments);
        this.allocationTable = allocationTable == null ? AllocationTable.empty() : allocationTable;
    }

    public static ExperimentGroup fromExistingExperiment(String id, String salt, ExperimentDefinition experimentDefinition) {
        List<AllocationRecord> records = experimentDefinition.renderRegularVariantsSolo().stream()
                .map(a -> new AllocationRecord(experimentDefinition.getId(), a.getVariantName(), a.getRange()))
                .collect(Collectors.toList());

        return new ExperimentGroup(id, salt, ImmutableList.of(experimentDefinition.getId()), new AllocationTable(records));
    }

    public static ExperimentGroup empty(String id, String salt) {
        return new ExperimentGroup(id, salt, Collections.emptyList(), AllocationTable.empty());
    }

    public ExperimentGroup addExperiment(ExperimentDefinition experiment) {
        Preconditions.checkArgument(experiment != null);

        List<String> extendedExperiments = join(experiments, experiment.getId());
        AllocationTable extendedTable = allocationTable.allocate(experiment);

        return new ExperimentGroup(id, salt, extendedExperiments, extendedTable);
    }

    public ExperimentGroup updateExperimentAllocation(ExperimentDefinition experiment) {
        Preconditions.checkArgument(experiment != null);

        AllocationTable extendedTable = allocationTable.allocate(experiment);
        return new ExperimentGroup(id, salt, experiments, extendedTable);
    }

    public AllocationTable getAllocationTable() {
        return allocationTable;
    }

    public int getMaxPossibleScaleUp(ExperimentDefinition experiment) {
        return allocationTable.getMaxPossibleAllocation(experiment.getId(), experiment.getNumberOfRegularVariants());
    }

    public boolean isAllocationPossible(ExperimentDefinition experiment) {
        return allocationTable.checkAllocation(experiment.getId(), experiment.getVariantNames(), experiment.getPercentage().get(), false);
    }

    public int getAllocationSumFor(String experimentId, String variant) {
        return allocationTable.getVariantAllocation(experimentId, variant);
    }

    public int getSharedBaseAllocationSum() {
        return allocationTable.getSharedBaseAllocationSum();
    }

    public List<PercentageRange> getAllocationFor(String experimentId, int percentage, String variant) {
        Preconditions.checkArgument(percentage > 0);
        List<AllocationRecord> allocatedRecords = getFullAllocationFor(experimentId, variant);
        return trimToCurrentAllocation(allocatedRecords, percentage, variant);
    }

    private List<AllocationRecord> getFullAllocationFor(String experimentId, String variant) {
        if (hasSharedBase()) {
            return getAllocationTable().getRecords().stream()
                    .filter(r -> r.belongsToOrShared(experimentId, variant))
                    .collect(Collectors.toList());
        } else {
            return getAllocationTable().getRecords().stream()
                    .filter(r -> r.belongsTo(experimentId, variant))
                    .collect(Collectors.toList());
        }
    }

    private List<PercentageRange> trimToCurrentAllocation(List<AllocationRecord> allocation, int targetPoints, String variant) {
        boolean fromRight = AllocationTable.BASE.equals(variant);

        List<AllocationRecord> sortedAllocation = allocation;
        if (fromRight) {
            sortedAllocation = reverse(sortedAllocation);
        }

        int renderedPoints = 0;
        List<PercentageRange> renderedRanges = new ArrayList();
        for (AllocationRecord allocatedRecord : sortedAllocation) {
            int missingPoints = targetPoints - renderedPoints;
            if (missingPoints <= 0) {
                break;
            }

            if (allocatedRecord.size() < missingPoints) {
                renderedPoints += allocatedRecord.size();
                renderedRanges.add(allocatedRecord.getRange());
            }
            else {
                renderedPoints += missingPoints;
                renderedRanges.add(allocatedRecord.getRange().trim(missingPoints,fromRight));
            }
        }
        return Collections.unmodifiableList(renderedRanges);
    }

    public String getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public List<String> getExperiments() {
        return experiments;
    }

    public boolean contains(String experimentId) {
        return experiments.contains(experimentId);
    }

    public boolean hasSharedBase() {
        return allocationTable.getSharedBaseAllocationSum() > 0;
    }

    public ExperimentGroup removeExperiment(String experimentId) {
        return new ExperimentGroup(id, salt, experiments.stream()
                .filter(e -> !e.equals(experimentId))
                .collect(Collectors.toList()), getAllocationTable().evict(experimentId));
    }

    @Override
    public int compareTo(ExperimentGroup o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "ExperimentGroup{" +
                "id='" + id + '\'' +
                '}';
    }
}