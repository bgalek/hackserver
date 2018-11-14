package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;

public class AllocationTable {
    final static String BASE = ExperimentVariant.BASE;
    final static String SHARED_BASE = "*";

    private final List<AllocationRecord> records;

    AllocationTable(List<AllocationRecord> records) {
        this.records = records == null ? Collections.emptyList() : ImmutableList.copyOf(records);
        validateRecords();
    }

    public List<AllocationRecord> getRecords() {
        return records;
    }

    int getMaxPossibleAllocation(String experimentId, int numberOfVariants) {
        Preconditions.checkArgument(experimentId != null);
        return (getExperimentAllocation(experimentId) + getFreeSum()) / numberOfVariants;
    }

    /**
     * Checks if there is enough space to allocate more percentage
     * for given number of variants (including base).
     */
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
        Collections.sort(newRecords, comparingInt(r -> r.getFrom()));

        return new AllocationTable(joinAdjacent(newRecords));
    }

    AllocationTable mergeAll(List<AllocationRecord> recordsToAdd) {
        AllocationTable result = this;

        for (AllocationRecord record : recordsToAdd) {
            result = result.merge(record);
        }

        return result;
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
        return records.stream().filter(it -> it.isSharedBase()).mapToInt(it -> it.size()).sum();
    }

    int getNonBaseAllocationSum() {
        return records.stream().filter(it -> !it.isBase()).mapToInt(it -> it.size()).sum();
    }

    int getAllocationSum() {
        return records.stream().mapToInt(it -> it.size()).sum();
    }

    int getFreeSum() {
        return 100 - getAllocationSum();
    }

    int getVariantAllocationShortage(String experimentId, String variant, int targetAllocation) {
        if (targetAllocation > getVariantAllocation(experimentId, variant)) {
            return targetAllocation - getVariantAllocation(experimentId, variant);
        }
        return 0;
    }

    int getVariantAllocation(String experimentId, String variant) {
        return records.stream()
                .filter(it -> it.belongsTo(experimentId, variant))
                .mapToInt(it -> it.size()).sum();
    }

    int getExperimentAllocation(String experimentId) {
        return records.stream()
                .filter(it -> it.belongsTo(experimentId))
                .mapToInt(it -> it.size()).sum();
    }

    public static AllocationTable empty() {
        return new AllocationTable(Collections.emptyList());
    }

    public boolean isEmpty() {
        return records.size() == 0;
    }

    private void validateRecords() {
        var allBuckets = new HashSet<Integer>();
        for (AllocationRecord r : records) {
            for (int b : r.getBuckets()) {
                if (allBuckets.contains(b)) {
                    throw new IllegalArgumentException("Ranges clash on bucket " + b);
                }
                allBuckets.add(b);
            }
        }

        for (int i=0; i<records.size()-1; i++) {
            AllocationRecord l = records.get(i);
            AllocationRecord r = records.get(i+1);

            if (l.getFrom() > r.getFrom()) {
                throw new IllegalArgumentException("Allocation records should be sorted, "+
                        "record["+i+"].from = " + l.getFrom()+" and record["+(i+1)+"].from = " + r.getFrom());
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
