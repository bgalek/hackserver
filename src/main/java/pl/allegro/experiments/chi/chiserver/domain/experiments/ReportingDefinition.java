package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReportingDefinition {
    private final List<EventDefinition> eventDefinitions;
    private final boolean gtm; // domain?
    private final boolean backendInteractionsEnabled;

    public ReportingDefinition(
            List<EventDefinition> eventDefinitions,
            boolean gtm,
            boolean backendInteractionsEnabled) {
        if (eventDefinitions != null) {
            this.eventDefinitions = ImmutableList.copyOf(eventDefinitions);
        } else {
            this.eventDefinitions = null;
        }
        this.gtm = gtm;
        this.backendInteractionsEnabled = backendInteractionsEnabled;
    }

    public List<EventDefinition> getEventDefinitions() {
        return Optional.ofNullable(eventDefinitions).map(ImmutableList::copyOf).orElse(null);
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

    public static ReportingDefinition createDefault() {
        return ReportingDefinition.backend();
    }

    static ReportingDefinition gtm() {
        return new ReportingDefinition(null, true, false);
    }

    static ReportingDefinition backend() {
        return new ReportingDefinition(null, false, true);
    }

    static ReportingDefinition frontend(List<EventDefinition> eventDefinitions) {
        return new ReportingDefinition(eventDefinitions, false, false);
    }
}
