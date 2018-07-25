package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportingDefinitionDeserializer implements Converter<Document, ReportingDefinition> {
    private final EventDefinitionDeserializer eventDefinitionDeserializer;

    public ReportingDefinitionDeserializer(EventDefinitionDeserializer eventDefinitionDeserializer) {
        this.eventDefinitionDeserializer = eventDefinitionDeserializer;
    }

    @Override
    public ReportingDefinition convert(Document source) {
        List<EventDefinition> eventDefinitions = Optional.ofNullable(source.get("eventDefinitions"))
                .map(rawEventDefinitions -> ((List<Document>) rawEventDefinitions).stream()
                        .map(eventDefinitionDeserializer::convert)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        Object reportingType = source.get("reportingType");
        if (reportingType != null) {
            return new ReportingDefinition(eventDefinitions, ReportingType.valueOf((String)reportingType));
        } else {
            boolean gtm = (boolean) source.get("gtm");
            boolean backendInteractionsEnabled = (boolean) source.get("backendInteractionsEnabled");
            return new ReportingDefinition(
                    eventDefinitions,
                    getReportingType(gtm, backendInteractionsEnabled)
            );
        }
    }

    private ReportingType getReportingType(boolean gtm, boolean backendInteractionsEnabled) {
        if (gtm) {
            return ReportingType.GTM;
        }
        if (backendInteractionsEnabled) {
            return ReportingType.BACKEND;
        }
        return ReportingType.FRONTEND;
    }
}