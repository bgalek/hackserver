package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.List;

public enum ReportingType {
    GTM, BACKEND, FRONTEND;

    public ReportingDefinition reportingDefinition(List<EventDefinition> eventDefinitions) {
        if (this.equals(GTM)) {
            return ReportingDefinition.gtm();
        }
        if (this.equals(BACKEND)) {
            return ReportingDefinition.backend();
        }
        return ReportingDefinition.frontend(eventDefinitions);
    }
}
