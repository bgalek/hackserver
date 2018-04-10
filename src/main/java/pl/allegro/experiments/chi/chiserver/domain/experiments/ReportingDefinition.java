package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ReportingDefinition withGtmEventDefinitionsIfGtm(ExperimentDefinition experimentDefinition) {
        if (getType().equals(ReportingType.GTM)) {
            List<String> variants = new ArrayList<>(experimentDefinition.getVariantNames());
            variants.add("base");
            return new ReportingDefinition(
                    variants.stream()
                            .map(v -> new EventDefinition("chiInteraction", experimentDefinition.getId(), null, v))
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
        return new ReportingDefinition(null, true, false);
    }

    static ReportingDefinition backend() {
        return new ReportingDefinition(null, false, true);
    }

    static ReportingDefinition frontend(List<EventDefinition> eventDefinitions) {
        return new ReportingDefinition(eventDefinitions, false, false);
    }
}
