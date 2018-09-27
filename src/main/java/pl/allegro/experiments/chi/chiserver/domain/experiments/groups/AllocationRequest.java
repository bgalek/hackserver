package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

class AllocationRequest {
    private final String expId;
    private final String variant;
    private final int targetPercentage;

    AllocationRequest(String expId, String variant, int targetPercentage) {
        this.expId = expId;
        this.variant = variant;
        this.targetPercentage = targetPercentage;
    }

    static AllocationRequest forSharedBase(int targetPercentage) {
        return new AllocationRequest(AllocationTable.SHARED_BASE, AllocationTable.BASE, targetPercentage);
    }

    boolean isBase() {
        return AllocationTable.BASE.equals(variant);
    }

    String getExpId() {
        return expId;
    }

    String getVariant() {
        return variant;
    }

    int getTargetPercentage() {
        return targetPercentage;
    }
}
