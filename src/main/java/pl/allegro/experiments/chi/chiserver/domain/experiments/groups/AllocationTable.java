package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.stream.Collectors;

public class AllocationTable {
    private final List<AllocationRecord> records;

    public AllocationTable(List<AllocationRecord> records) {
        validate(records);
        System.out.println("records = [" + records + "]");
        this.records = ImmutableList.copyOf(records);
    }

    List<AllocationRecord> getRecords() {
        return records;
    }


    /**
     * Checks if there is enough space to allocate more percentage
     * for given number of variants and base.
     */
    boolean checkAllocation(int numberOfVariants, int percentage){
        return getBaseAllocation() + percentage +
               getNonBaseAllocation() + percentage * numberOfVariants <= 100;
    }

    int getBaseAllocation(){
        return records.stream().filter(it -> it.isBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    int getNonBaseAllocation() {
        return records.stream().filter(it -> !it.isBase()).mapToInt(it -> it.getAllocation()).sum();
    }

    public static AllocationTable empty() {
        return new AllocationTable(Collections.emptyList());
    }

    public boolean isEmpty() {
        return records.size() == 0;
    }

    private void validate(List<AllocationRecord> records) {
        var allBuckets = new HashSet<Integer>();

        for (AllocationRecord r : records) {
            for (int b : r.getBuckets()) {
                if (allBuckets.contains(b)) {
                    throw new IllegalArgumentException("ranges clash on bucket " + b);
                }
                allBuckets.add(b);
            }
        }

        for (AllocationRecord r : records) {
            if (r.isBase() && !"*".equals(r.getExperimentId())) {
                throw new IllegalArgumentException("attempt to assign base range exclusively to experiment "+ r.getExperimentId());
            }
        }
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
