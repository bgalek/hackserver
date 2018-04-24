package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminExperiment {
    private ExperimentDefinition experimentDefinition;
    private final ExperimentOrigin origin;
    private final boolean editable;
    private final ClientExperiment clientExperiment;
    private ExperimentMeasurements experimentMeasurements;

    public AdminExperiment(Experiment legacyDefinition) {
        experimentDefinition = fromLegacyDefinition(legacyDefinition);
        origin = ExperimentOrigin.STASH;
        editable = false;
        clientExperiment = new ClientExperiment(legacyDefinition);
    }

    public AdminExperiment(ExperimentDefinition experimentDefinition, User currentUser) {
        this.experimentDefinition = experimentDefinition;
        origin = ExperimentOrigin.MONGO;
        editable = currentUser.isOwner(experimentDefinition);
        clientExperiment = new ClientExperiment(experimentDefinition.toExperiment());
    }

    private static ExperimentDefinition fromLegacyDefinition(Experiment legacyDefinition) {
        return ExperimentDefinition.builder()
                .id(legacyDefinition.getId())
                .activityPeriod(legacyDefinition.getActivityPeriod())
                .variantNames(legacyDefinition.getVariants().stream().map(it->it.getName()).collect(Collectors.toList()))
                .description(legacyDefinition.getDescription())
                .documentLink(legacyDefinition.getDocumentLink())
                .author(legacyDefinition.getAuthor())
                .groups(legacyDefinition.getGroups())
                .reportingEnabled(legacyDefinition.getReportingEnabled())
                .reportingDefinition(ReportingDefinition.createDefault())
                .explicitStatus(legacyDefinition.getExplicitStatus())
                .build();
    }

    public AdminExperiment withMeasurements(ExperimentMeasurements experimentMeasurements) {
        Preconditions.checkState(this.experimentMeasurements == null);
        this.experimentMeasurements = experimentMeasurements;
        return this;
    }

    public ExperimentMeasurements getMeasurements() {
        return this.experimentMeasurements;
    }

    public String getOrigin() {
        return origin.name();
    }

    public List<ExperimentVariant> getRenderedVariants() {
        return clientExperiment.getVariants();
    }

    public String getId() {
        return experimentDefinition.getId();
    }

    public List<String> getVariantNames() {
        return experimentDefinition.getVariantNames();
    }

    public Optional<String> getInternalVariantName() {
        return experimentDefinition.getInternalVariantName();
    }

    public Optional<Integer> getPercentage() {
        return experimentDefinition.getPercentage();
    }

    public Optional<String> getDeviceClass() {
        return this.experimentDefinition.getDeviceClass();
    }

    public String getDescription() {
        return this.experimentDefinition.getDescription();
    }

    public String getDocumentLink() {
        return this.experimentDefinition.getDocumentLink();
    }

    public String getAuthor() {
        return this.experimentDefinition.getAuthor();
    }

    public List<String> getGroups() {
        return this.experimentDefinition.getGroups();
    }

    public boolean isReportingEnabled() {
        return this.experimentDefinition.isReportingEnabled();
    }

    public ActivityPeriod getActivityPeriod() {
        return this.experimentDefinition.getActivityPeriod();
    }

    public Boolean getEditable() {
        return this.editable;
    }

    public ExperimentStatus getStatus() {
        return this.experimentDefinition.getStatus();
    }

    public List<EventDefinition> getEventDefinitions() {
        return experimentDefinition.getReportingDefinition().getEventDefinitions();
    }

    public ReportingType getReportingType() {
        return experimentDefinition.getReportingDefinition().getType();
    }
}
