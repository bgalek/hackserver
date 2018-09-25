package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable.BASE;

class SpaceAllocator {
    static AllocationTable allocateNewRecords(AllocationTable oldTable, String experimentId, List<String> variantNames, int percentage) {
        AllocationTable currentTable = oldTable;

        for(String variant : variantNames) {
            if (BASE.equals(variant)) {
                int alreadyAllocated = currentTable.getBaseAllocation();
                if (alreadyAllocated < percentage) {
                    currentTable = currentTable.merge(allocateBase(currentTable, percentage - alreadyAllocated));
                }
            }
            else {
                int alreadyAllocated = currentTable.getVariantAllocation(experimentId, variant);
                if (alreadyAllocated < percentage) {
                    currentTable = currentTable.merge(allocateVariant(currentTable, percentage - alreadyAllocated, experimentId, variant));
                }
            }
        }
        return currentTable;
    }

    private static List<AllocationRecord> allocateBase(AllocationTable table, int percentage) {
        return allocate((p) -> allocateNextChunkFromRight(table,p), percentage).stream()
                .map(it -> AllocationRecord.forBase(it))
                .collect(Collectors.toList());
    }

    private static List<AllocationRecord> allocateVariant(AllocationTable table, int percentage, String experimentId, String variant) {
        return allocate((p) -> allocateNextChunkFromLeft(table, p), percentage).stream()
                .map(it -> AllocationRecord.forVariant(experimentId, variant, it))
                .collect(Collectors.toList());
    }

    private static List<PercentageRange> allocate(Function<Integer, PercentageRange> freeChunkSupplier, int percentage) {
        int allocated = 0;
        var result = new ArrayList<PercentageRange>();

        while (allocated < percentage) {
            PercentageRange newChunk = freeChunkSupplier.apply(percentage - allocated);
            allocated += newChunk.size();
            result.add(newChunk);
        }

        return Collections.unmodifiableList(result);
    }

    private static PercentageRange allocateNextChunkFromLeft(AllocationTable table, int percentage) {
        return allocateNextChunk(table.buckets(), percentage);
    }

    private static PercentageRange allocateNextChunkFromRight(AllocationTable table, int percentage) {
        return allocateNextChunk(table.bucketsReversed(), percentage);
    }

    private static PercentageRange allocateNextChunk(List<Bucket> buckets, int percentage) {
        int allocated = 0;
        Bucket stop = null;
        Bucket start = null;

        for (Bucket bucket : buckets) {
            if (allocated == percentage) break;

            if (bucket.isFree() && start == null) {
                start = bucket.taken();
                stop = start;
                allocated++;
                continue;
            }

            if (bucket.isFree()) {
                stop = bucket.taken();
                allocated++;
            }
        }

        if (stop == null || start == null) {
            throw new IllegalStateException("No free buckets, something is really wrong");
        }

        if (start.getId() >= stop.getId()) {
            return new PercentageRange(stop.getId(), start.getId() + 1);
        } else {
            return new PercentageRange(start.getId(), stop.getId() + 1);
        }
    }
}
