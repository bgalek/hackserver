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

    public AllocationTable getAllocationTable() {
        return allocationTable;
    }

    public boolean checkAllocation(ExperimentDefinition experiment) {
        return allocationTable.checkAllocation(experiment);
    }

    public int getAllocationSumFor(String experimentId, String variant) {
        return allocationTable.getVariantAllocation(experimentId, variant);
    }

    public int getSharedBaseAllocationSum() {
        return allocationTable.getSharedBaseAllocationSum();
    }

    public List<PercentageRange> getAllocationFor(String experimentId, String variant) {
        if (hasSharedBase()) {
            return getAllocationTable().getRecords().stream()
                    .filter(r -> r.belongsToOrShared(experimentId, variant))
                    .map(r -> r.getRange())
                    .collect(Collectors.toList());
        } else {
            return getAllocationTable().getRecords().stream()
                    .filter(r -> r.belongsTo(experimentId, variant))
                    .map(r -> r.getRange())
                    .collect(Collectors.toList());
        }
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

    public ExperimentGroup addExperiment(ExperimentDefinition experiment) {
        Preconditions.checkArgument(experiment != null);

        allocationTable.checkAllocation(experiment);

        List<String> extendedExperiments = new ArrayList<>(experiments);
        extendedExperiments.add(experiment.getId());

        AllocationTable extendedTable = allocationTable.allocate(experiment);

        return new ExperimentGroup(id, salt, extendedExperiments, extendedTable);
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