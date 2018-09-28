package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class AllocationTable {
    final static String BASE = "base";
    final static String SHARED_BASE = "*";

    private final List<AllocationRecord> records;

    AllocationTable(List<AllocationRecord> records) {
        this.records = records == null ? Collections.emptyList() : ImmutableList.copyOf(records);
        validateRecords(this.records);
    }

    List<AllocationRecord> getRecords() {
        return records;
    }

    /**
     * Checks if there is enough space to allocate more percentage
     * for given number of variants (including base).
     */
    boolean checkAllocation(ExperimentDefinition experiment) {
        return checkAllocation(experiment.getId(), experiment.getVariantNames(), experiment.getPercentage().get());
    }

    boolean checkAllocation(final String experimentId, List<String> variantNames, final int targetPercentage) {
        return checkAllocation(experimentId, variantNames, targetPercentage, false);
    }

    boolean checkAllocation(final String experimentId, List<String> variantNames, final int targetPercentage, boolean sharedBase) {
        Preconditions.checkArgument(experimentId != null);
        Preconditions.checkArgument(variantNames != null);

        if (sharedBase) {
            return  Math.max(getSharedBaseAllocationSum(), targetPercentage) +
                    variantNames.stream()
                            .filter(this::isNotBase)
                            .mapToInt(v -> getVariantAllocationShortage(experimentId, v, targetPercentage))
                            .sum() + getNonBaseAllocationSum() <= 100;
        } else {
            return variantNames.stream()
                           .mapToInt(v -> getVariantAllocationShortage(experimentId, v, targetPercentage))
                           .sum() + getAllocationSum() <= 100;
        }
    }

    /**
     * Allocates percentage for a given experiment
     */
    public AllocationTable allocate(ExperimentDefinition experiment) {
        return allocate(experiment.getId(), experiment.getVariantNames(), experiment.getPercentage().get());
    }

    public AllocationTable allocate(String experimentId, List<String> variantNames, int targetPercentage) {
        return allocate(experimentId, variantNames, targetPercentage, false);
    }

    public AllocationTable allocate(String experimentId, List<String> variantNames, int targetPercentage, boolean sharedBase) {
        validateAllocation(experimentId, variantNames, targetPercentage, sharedBase);

        var newAllocation = variantNames.stream().map(v -> {
                    if(isBase(v) && sharedBase) {
                        return AllocationRequest.forSharedBase(targetPercentage);
                    }
                    return new AllocationRequest(experimentId, v, targetPercentage);
                }).collect(Collectors.toList());

        return SpaceAllocator.allocateNewRecords(this, newAllocation);
    }

    AllocationTable evict(String experimentId) {
        Preconditions.checkArgument(experimentId != null);
        Preconditions.checkArgument(!experimentId.equals(SHARED_BASE), "cheating!");

        List<AllocationRecord> newRecords = records.stream()
                .filter(record -> !record.belongsTo(experimentId))
                .collect(Collectors.toList());

        if (newRecords.size() == 1 && newRecords.get(0).isSharedBase()) {
            return new AllocationTable(Collections.emptyList());
        }
        return new AllocationTable(newRecords);
    }

    AllocationTable merge(AllocationRecord recordToAdd) {
        List<AllocationRecord> newRecords = new ArrayList<>(records);
        newRecords.add(recordToAdd);
        Collections.sort(newRecords, Comparator.comparingInt(r -> r.getRange().getFrom()));

        return new AllocationTable(joinAdjacent(newRecords));
    }

    List<Bucket> buckets() {
        var buckets = new ArrayList<Bucket>();

        for (int i = 0; i < 100; i++) buckets.add(Bucket.free(i));

        for (AllocationRecord r : records) {
            for (int b : r.getBuckets()) {
                buckets.set(b, Bucket.taken(b));
            }
        }

        return Collections.unmodifiableList(buckets);
    }

    List<Bucket> bucketsReversed() {
        return Lists.reverse(buckets());
    }

    private void validateAllocation(String experimentId, List<String> variantNames, int percentage, boolean sharedBase) {
        if (!checkAllocation(experimentId, variantNames, percentage, sharedBase)) {
            throw new IllegalArgumentException("You are pushing to hard, relax!");
        }

        if(!variantNames.contains(BASE)) {
            throw new IllegalArgumentException("Attempt to allocate experiment " + experimentId + " without base.");
        }
    }

    int getSharedBaseAllocationSum() {
        return records.stream().filter(it -> it.isSharedBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    int getNonBaseAllocationSum() {
        return records.stream().filter(it -> !it.isBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    int getAllocationSum() {
        return records.stream().mapToInt(it -> it.getAllocation()).sum();
    }

    int getVariantAllocationShortage(String experimentId, String variant, int targetAllocation) {
        if (targetAllocation > getVariantAllocation(experimentId, variant)) {
            return targetAllocation - getVariantAllocation(experimentId, variant);
        }
        return 0;
    }

    int getVariantAllocation(String experimentId, String variant) {
        return records.stream().filter(it -> it.getExperimentId().equals(experimentId) && it.getVariant().equals(variant)).mapToInt(it -> it.getAllocation()).sum();
    }

    public static AllocationTable empty() {
        return new AllocationTable(Collections.emptyList());
    }

    public boolean isEmpty() {
        return records.size() == 0;
    }

    private void validateRecords(List<AllocationRecord> records) {
        var allBuckets = new HashSet<Integer>();
        for (AllocationRecord r : records) {
            for (int b : r.getBuckets()) {
                if (allBuckets.contains(b)) {
                    throw new IllegalArgumentException("Ranges clash on bucket " + b);
                }
                allBuckets.add(b);
            }
        }
    }

    private static List<AllocationRecord> joinAdjacent(List<AllocationRecord> records) {
        List<AllocationRecord> joined = new ArrayList<>();

        for (int i=0; i<records.size(); i++) {
            AllocationRecord left = records.get(i);
            AllocationRecord right = i + 1 <records.size() ? records.get(i + 1) : null;

            if (right != null && left.canJoinWith(right)) {
                joined.add(left.joined(right));
                i++;
            } else {
                joined.add(left);
            }
        }

        return joined;
    }

    private boolean isNotBase(String variant) {
        return !isBase(variant);
    }

    private boolean isBase(String variant) {
        return BASE.equals(variant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllocationTable that = (AllocationTable) o;
        return Objects.equals(records, that.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(records);
    }
}
