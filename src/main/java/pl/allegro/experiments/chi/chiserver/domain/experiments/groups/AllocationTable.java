package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.*;

public class AllocationTable {
    final static String BASE = "base";
    private final List<AllocationRecord> records;

    public AllocationTable(List<AllocationRecord> records) {
        validateRecords(records);
        this.records = ImmutableList.copyOf(records);
    }

    List<AllocationRecord> getRecords() {
        return records;
    }


    /**
     * Checks if there is enough space to allocate more percentage
     * for given number of variants (including base).
     */
    boolean checkAllocation(int numberOfVariants, int percentage) {
        return getBaseAllocation() + percentage +
               getNonBaseAllocation() + percentage * (numberOfVariants - 1) <= 100;
    }

    /**
     * Allocates percentage for a given variants (including base).
     */
    AllocationTable allocate(String experimentId, List<String> variantNames, int percentage) {
        validateAllocation(experimentId, variantNames, percentage);

        return SpaceAllocator.allocateNewRecords(this, experimentId, variantNames, percentage);
    }

    AllocationTable merge(List<AllocationRecord> recordsToAdd) {
        List<AllocationRecord> newRecords = new ArrayList<>(records);
        newRecords.addAll(recordsToAdd);
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

    private void validateAllocation(String experimentId, List<String> variantNames, int percentage) {
        if (!checkAllocation(variantNames.size(), percentage)) {
            throw new IllegalArgumentException("You are pushing to hard, relax!");
        }

        if(!variantNames.contains(BASE)) {
            throw new IllegalArgumentException("Attempt to allocate experiment " + experimentId + " without base.");
        }
    }

    int getBaseAllocation() {
        return records.stream().filter(it -> it.isBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    int getNonBaseAllocation() {
        return records.stream().filter(it -> !it.isBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    int getVariantAllocation( String experimentId, String variant) {
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

        for (AllocationRecord r : records) {
            if (r.isBase() && !"*".equals(r.getExperimentId())) {
                throw new IllegalArgumentException("Attempt to assign base range exclusively to experiment "+ r.getExperimentId());
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
