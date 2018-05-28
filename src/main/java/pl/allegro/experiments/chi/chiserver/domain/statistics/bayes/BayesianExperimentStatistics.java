package pl.allegro.experiments.chi.chiserver.domain.statistics.bayes;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BayesianExperimentStatistics {
    private static final Logger logger = LoggerFactory.getLogger(BayesianExperimentStatistics.class);

    private final String experimentId;
    private final String toDate;
    private final String device;
    private final List<VariantBayesianStatistics> variantBayesianStatistics;

    public BayesianExperimentStatistics(String experimentId,
                                        String toDate,
                                        String device,
                                        List<VariantBayesianStatistics> variantBayesianStatistics) {
        Preconditions.checkNotNull(experimentId, "experimentId cannot be null");
        Preconditions.checkNotNull(toDate, "toDate cannot be null");
        Preconditions.checkNotNull(device, "device cannot be null");
        Preconditions.checkNotNull(variantBayesianStatistics, "variantBayesianStatistics cannot be null");

        this.experimentId = experimentId;
        this.toDate = toDate;
        this.device = device;
        this.variantBayesianStatistics = variantBayesianStatistics;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getDevice() {
        return device;
    }

    public String getToDate() {
        return toDate;
    }

    public List<VariantBayesianStatistics> getVariantBayesianStatistics() {
        return variantBayesianStatistics;
    }

    public BayesianExperimentStatistics updateVariants(BayesianExperimentStatistics statsWithNewVariants) {
        Preconditions.checkArgument(this.experimentId.equals(statsWithNewVariants.experimentId));
        Preconditions.checkArgument(this.device.equals(statsWithNewVariants.device));
        Preconditions.checkArgument(this.toDate.equals(statsWithNewVariants.toDate));

        logger.info("updateVariants of {} {} {}", experimentId, device, toDate);
        logger.info(".. existing Variants: {}", this.variantBayesianStatistics.stream().map(it -> it.getVariantName()).collect(Collectors.toList()));

        logger.info(".. got new stats - {} {} {}", statsWithNewVariants.experimentId, statsWithNewVariants.device, statsWithNewVariants.toDate);
        logger.info(".. new Variants: {}", statsWithNewVariants.variantBayesianStatistics.stream().map(it -> it.getVariantName()).collect(Collectors.toList()));

        Map<String, VariantBayesianStatistics> oldVariants = this.variantBayesianStatistics.stream().collect(Collectors.toMap(VariantBayesianStatistics::getVariantName, e -> e));
        Map<String, VariantBayesianStatistics> newVariants = statsWithNewVariants.variantBayesianStatistics.stream().collect(Collectors.toMap(VariantBayesianStatistics::getVariantName, e -> e));

        oldVariants.putAll(newVariants);

        var updatedStats = new BayesianExperimentStatistics(experimentId, toDate, device, oldVariants.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));

        logger.info(".. merged Variants: {}", updatedStats.variantBayesianStatistics.stream().map(it -> it.getVariantName()).collect(Collectors.toList()));

        return updatedStats;
    }

    @Override
    public String toString() {
        return "BayesianExperimentStatistics {" +
                "experimentId='" + experimentId + '\'' +
                ", toDate=" + toDate +
                ", device='" + device + '\'' +
                ", variantBayesianStatistics=/array of " + variantBayesianStatistics.size() + "/\n" +
                '}';
    }
}

