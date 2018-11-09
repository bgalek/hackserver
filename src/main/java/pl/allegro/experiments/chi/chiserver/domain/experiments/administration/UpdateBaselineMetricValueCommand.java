package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.calculator.SampleSizeCalculator;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric;

class UpdateBaselineMetricValueCommand implements ExperimentCommand {
    private static final Logger logger = LoggerFactory.getLogger(UpdateBaselineMetricValueCommand.class);

    private final ExperimentsRepository experimentsRepository;
    private final SampleSizeCalculator sampleSizeCalculator;
    private final ClassicExperimentStatisticsForVariantMetric currentStats;

    UpdateBaselineMetricValueCommand(ClassicExperimentStatisticsForVariantMetric currentStats,
                                     ExperimentsRepository experimentsRepository,
                                     SampleSizeCalculator sampleSizeCalculator) {
        this.currentStats = currentStats;
        this.experimentsRepository = experimentsRepository;
        this.sampleSizeCalculator = sampleSizeCalculator;
    }

    public void execute() {
        ExperimentDefinition experiment = experimentsRepository.getExperiment(currentStats.getExperimentId())
                .orElse(null);

        if (shouldBeExecuted(experiment)) {
            var newBaselineMetricValuePercent = currentStats.getData().getValue() * 100;
            var newCurrentSampleSize = currentStats.getData().getCount();

            logger.info("updating baselineMetricValue of experiment {} to {}, device: {}, metric: {}, count: {}", experiment.getId(), newBaselineMetricValuePercent, currentStats.getDevice(), currentStats.getMetricName(), newCurrentSampleSize);
            var mutatedGoal = experiment.getGoal().get().updateBaselineMetricValue(newBaselineMetricValuePercent);

            var newRequiredSampleSize = sampleSizeCalculator.calculateSampleSize(mutatedGoal);
            mutatedGoal = mutatedGoal.updateRequiredSampleSize(newRequiredSampleSize, newCurrentSampleSize);

            ExperimentDefinition mutated = experiment.mutate().goal(mutatedGoal).build();
            experimentsRepository.save(mutated);
        }
    }

    private boolean shouldBeExecuted(ExperimentDefinition experiment) {
        if (experiment == null || !"base".equals(currentStats.getVariantName())) {
            return false;
        }

        if (!experiment.getGoal().isPresent()) {
            return false;
        }

        var goal = experiment.getGoal().get();

        return goal.getHypothesis().getLeadingMetric().equals(currentStats.getMetricName()) &&
               experiment.getDeviceClass().orElse(DeviceClass.all) == currentStats.getDevice();

    }

    @Override
    public String getNotificationMessage() {
        throw new NotImplementedException("this command should not be notified");
    }

    @Override
    public String getExperimentId() {
        return currentStats.getExperimentId();
    }
}
