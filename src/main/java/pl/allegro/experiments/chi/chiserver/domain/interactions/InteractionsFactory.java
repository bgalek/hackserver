package pl.allegro.experiments.chi.chiserver.domain.interactions;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.application.interactions.v1.InvalidFormatException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InteractionsMetricsReporter;

import java.util.List;
import java.util.stream.Collectors;

public class InteractionsFactory {
    private final InteractionsMetricsReporter interactionsMetricsReporter;
    private final ExperimentsRepository experimentsRepository;
    private final InteractionConverter interactionConverter;

    public InteractionsFactory(
            InteractionsMetricsReporter interactionsMetricsReporter,
            ExperimentsRepository experimentsRepository,
            InteractionConverter interactionConverter) {
        Preconditions.checkNotNull(interactionConverter);
        Preconditions.checkNotNull(interactionsMetricsReporter);
        Preconditions.checkNotNull(experimentsRepository);
        this.interactionsMetricsReporter = interactionsMetricsReporter;
        this.experimentsRepository = experimentsRepository;
        this.interactionConverter = interactionConverter;
    }

    public List<Interaction> fromJson(String json) {
        Preconditions.checkNotNull(json);
        try {
            List<Interaction> interactions = interactionConverter.fromJson(json);
            List<Interaction> filtered = interactions.stream().filter(i ->
                experimentsRepository.getExperiment(i.getExperimentId())
                        .map(Experiment::getReportingEnabled)
                        .orElse(false)
            ).collect(Collectors.toList());
            interactionsMetricsReporter.meterIgnored(interactions.size() - filtered.size());
            return filtered;
        } catch (Exception e) {
            throw new InvalidFormatException("Cant deserialize Interaction. Invalid format.");
        }
    }
}
