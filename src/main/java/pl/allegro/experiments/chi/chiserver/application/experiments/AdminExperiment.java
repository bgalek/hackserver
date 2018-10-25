package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianHorizontalEqualizer;
import pl.allegro.experiments.chi.chiserver.domain.statistics.ExperimentMeasurements;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperiment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class AdminExperiment {
    private ExperimentDefinition experimentDefinition;
    private final boolean editable;
    private final ClientExperiment clientExperiment;
    private final int maxPossibleAllocation;
    private ExperimentMeasurements experimentMeasurements;
    private BayesianHorizontalEqualizer bayesianHorizontalEqualizer;
    private ExperimentGroup experimentGroup;
    private int bonferroniCorrection;

    public AdminExperiment(
            ExperimentDefinition experimentDefinition,
            User currentUser,
            ClientExperiment clientExperiment,
            int maxPossibleAllocation) {
        this.experimentDefinition = experimentDefinition;
        this.editable = currentUser.isOwner(experimentDefinition);
        this.clientExperiment = clientExperiment;
        this.maxPossibleAllocation = maxPossibleAllocation;
    }

    AdminExperiment withHorizontalEqualizer(BayesianHorizontalEqualizer equalizer) {
        this.bayesianHorizontalEqualizer = equalizer;
        return this;
    }

    AdminExperiment withMeasurements(ExperimentMeasurements experimentMeasurements) {
        Preconditions.checkState(this.experimentMeasurements == null);
        this.experimentMeasurements = experimentMeasurements;
        return this;
    }

    AdminExperiment withExperimentGroup(ExperimentGroup experimentGroup) {
        Preconditions.checkState(this.experimentGroup == null);
        this.experimentGroup = experimentGroup;
        return this;
    }

    AdminExperiment withBonferroniCorrection(int bonferroniCorrection) {
        this.bonferroniCorrection = bonferroniCorrection;
        return this;
    }

    public int getBonferroniCorrection() {
        return bonferroniCorrection;
    }

    public ExperimentMeasurements getMeasurements() {
        return this.experimentMeasurements;
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
        return this.experimentDefinition.getDeviceClass().map(it -> it.toJsonString());
    }

    public ExperimentGoal getGoal() {
        return experimentDefinition.getGoal();
    }

    public BayesianHorizontalEqualizer getBayesianHorizontalEqualizer() {
        return bayesianHorizontalEqualizer;
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

    public ActivityPeriod getActivityPeriod() {
        return this.experimentDefinition.getActivityPeriod();
    }

    public Boolean getEditable() {
        return this.editable;
    }

    public ExperimentStatus getStatus() {
        return this.experimentDefinition.getStatus();
    }

    public int getMaxPossibleAllocation() {
        return maxPossibleAllocation;
    }

    public boolean isActive() {
        return getStatus().equals(ExperimentStatus.ACTIVE);
    }

    public List<EventDefinition> getEventDefinitions() {
        return experimentDefinition.getReportingDefinition().getEventDefinitions();
    }

    public ReportingType getReportingType() {
        return experimentDefinition.getReportingDefinition().getType();
    }

    public ExperimentGroup getExperimentGroup() {
        return experimentGroup;
    }

    public ZonedDateTime getLastStatusChange() {
        return experimentDefinition.getLastStatusChange();
    }
}
