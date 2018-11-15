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
    private final String metricName;
    private final String toDate;
    private final String device;
    private final List<VariantBayesianStatistics> variantBayesianStatistics;

    public BayesianExperimentStatistics(String experimentId,
                                        String metricName,
                                        String toDate,
                                        String device,
                                        List<VariantBayesianStatistics> variantBayesianStatistics) {
        Preconditions.checkNotNull(experimentId, "experimentId can't be null");
        Preconditions.checkNotNull(metricName, "metricName can't be null");
        Preconditions.checkNotNull(toDate, "toDate can't be null");
        Preconditions.checkNotNull(device, "device can't be null");
        Preconditions.checkNotNull(variantBayesianStatistics, "variantBayesianStatistics cannot be null");

        this.experimentId = experimentId;
        this.toDate = toDate;
        this.device = device;
        this.variantBayesianStatistics = variantBayesianStatistics;
        this.metricName = metricName;
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

    public String getMetricName() {
        return metricName;
    }

    public List<VariantBayesianStatistics> getVariantBayesianStatistics() {
        return variantBayesianStatistics;
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

