package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

public class ReportingDefinition {
    private final List<EventDefinition> eventDefinitions;
    private final boolean gtm; // domain?
    private final boolean backendInteractionsEnabled;

    private ReportingDefinition(
            List<EventDefinition> eventDefinitions,
            boolean gtm,
            boolean backendInteractionsEnabled) {
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

    static ReportingDefinition createDefault() {
        return ReportingDefinition.backend();
    }

    static ReportingDefinition gtm() {
        return new ReportingDefinition(null, true, false);
    }

    static ReportingDefinition backend() {
        return new ReportingDefinition(Collections.emptyList(), false, true);
    }

    static ReportingDefinition frontend(List<EventDefinition> eventDefinitions) {
        return new ReportingDefinition(eventDefinitions, false, false);
    }
}
