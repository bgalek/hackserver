package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportingDefinition {
    private final List<EventDefinition> eventDefinitions;
    private final ReportingType reportingType;

    public ReportingDefinition(
            List<EventDefinition> eventDefinitions,
            ReportingType reportingType) {
        Preconditions.checkNotNull(eventDefinitions);
        Preconditions.checkNotNull(reportingType);
        this.eventDefinitions = ImmutableList.copyOf(eventDefinitions);
        this.reportingType = reportingType;
    }

    public List<EventDefinition> getEventDefinitions() {
        return ImmutableList.copyOf(eventDefinitions);
    }

    public boolean isGtm() {
        return reportingType.equals(ReportingType.GTM);
    }

    public boolean isBackendInteractionsEnabled() {
        return true;
    }

    public ReportingType getType() {
        return reportingType;
    }

    public ReportingDefinition withImplicitEventDefinitionsIfGtm(ExperimentDefinition experimentDefinition) {
        if (getType().equals(ReportingType.GTM)) {
            return new ReportingDefinition(
                    experimentDefinition.getVariantNames().stream()
                            .map(v -> new EventDefinition("chiInteraction", experimentDefinition.getId(), null, v, null))
                            .collect(Collectors.toList()),
                    ReportingType.GTM
            );
        }
        return this;
    }

    public static ReportingDefinition createDefault() {
        return ReportingDefinition.backend();
    }

    static ReportingDefinition gtm() {
        return new ReportingDefinition(Collections.emptyList(), ReportingType.GTM);
    }

    static ReportingDefinition backend() {
        return new ReportingDefinition(Collections.emptyList(), ReportingType.BACKEND);
    }

    public static ReportingDefinition frontend(List<EventDefinition> eventDefinitions) {
        return new ReportingDefinition(eventDefinitions, ReportingType.FRONTEND);
    }

    @Override
    public String toString() {
        String result = String.join("\n", eventDefinitions.stream()
                .map(EventDefinition::toString)
                .collect(Collectors.toList()));
        return result.isEmpty() ? "empty" : result;
    }
}
