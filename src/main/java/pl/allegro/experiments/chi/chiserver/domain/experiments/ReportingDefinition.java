package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportingDefinition {
    private final List<EventDefinition> eventDefinitions;
    private final boolean gtm;
    private final boolean backendInteractionsEnabled;

    public ReportingDefinition(
            List<EventDefinition> eventDefinitions,
            boolean gtm,
            boolean backendInteractionsEnabled) {
        Preconditions.checkNotNull(eventDefinitions);
        this.eventDefinitions = ImmutableList.copyOf(eventDefinitions);
        this.gtm = gtm;
        this.backendInteractionsEnabled = backendInteractionsEnabled;
    }

    public List<EventDefinition> getEventDefinitions() {
        return ImmutableList.copyOf(eventDefinitions);
    }

    public boolean isGtm() {
        return gtm;
    }

    public boolean isBackendInteractionsEnabled() {
        return backendInteractionsEnabled;
    }

    public ReportingType getType() {
        if (gtm) {
            return ReportingType.GTM;
        }
        if (backendInteractionsEnabled) {
            return ReportingType.BACKEND;
        }
        return ReportingType.FRONTEND;
    }

    public ReportingDefinition withImplicitEventDefinitionsIfGtm(ExperimentDefinition experimentDefinition) {
        if (getType().equals(ReportingType.GTM)) {
            return new ReportingDefinition(
                    experimentDefinition.getVariantNames().stream()
                            .map(v -> new EventDefinition("chiInteraction", experimentDefinition.getId(), null, v, null))
                            .collect(Collectors.toList()),
                    true,
                    false
            );
        }
        return this;
    }

    public static ReportingDefinition createDefault() {
        return ReportingDefinition.backend();
    }

    static ReportingDefinition gtm() {
        return new ReportingDefinition(Collections.emptyList(), true, false);
    }

    static ReportingDefinition backend() {
        return new ReportingDefinition(Collections.emptyList(), false, true);
    }

    public static ReportingDefinition frontend(List<EventDefinition> eventDefinitions) {
        return new ReportingDefinition(eventDefinitions, false, false);
    }
}
