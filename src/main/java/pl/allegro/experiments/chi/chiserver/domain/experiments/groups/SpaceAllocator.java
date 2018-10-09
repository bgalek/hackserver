package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;

import java.util.List;

class SpaceAllocator {
    static AllocationTable allocateNewRecords(AllocationTable oldTable, List<AllocationRequest> newAllocation) {
        AllocationTable currentTable = oldTable;

        for(AllocationRequest req : newAllocation) {
            int wannaAllocate = req.getTargetPercentage() - currentTable.getVariantAllocation(req.getExpId(), req.getVariant());

            while (wannaAllocate > 0) {
                currentTable = currentTable.merge(allocateNextRecord(currentTable, wannaAllocate, req.getExpId(), req.getVariant()));
                wannaAllocate = req.getTargetPercentage() - currentTable.getVariantAllocation(req.getExpId(), req.getVariant());
            }

        }
        return currentTable;
    }

    private static AllocationRecord allocateNextRecord(AllocationTable table, int wannaAllocate, String experimentId, String variant) {
        boolean startFromLeft = !AllocationTable.BASE.equals(variant);

        PercentageRange newChunk = allocateNextChunk(table, wannaAllocate, startFromLeft);

        return AllocationRecord.forVariant(experimentId, variant, newChunk);
    }

    private static PercentageRange allocateNextChunk(AllocationTable table, int wannaAllocate, boolean startFromLeft) {
        List<Bucket> buckets = startFromLeft ? table.buckets() : table.bucketsReversed();

        int allocated = 0;
        Bucket stop = null;
        Bucket start = null;

        for (Bucket bucket : buckets) {
            if (allocated == wannaAllocate) break;

            if (bucket.isFree() && start == null) {
                start = bucket.taken();
                stop = start;
                allocated++;
                continue;
            }

            if (!bucket.isFree() && start != null) {
                break; //stop
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
