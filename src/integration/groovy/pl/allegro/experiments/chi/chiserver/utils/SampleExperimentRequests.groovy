package pl.allegro.experiments.chi.chiserver.utils

import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingType
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest

class SampleExperimentRequests {
    static Map DEFAULT_PROPERTIES = [
            variantNames       : ['v1'],
            percentage         : 10,
            internalVariantName: null,
            deviceClass        : null,
            description        : null,
            documentLink       : null,
            groups             : null,
            reportingType      : null,
            customParameterName: null,
            customParamterValue: null,
            eventDefinitions   : null
    ]

    static Map sampleExperimentCreationRequestProperties(Map customProperties = [:]) {
        withRandomId(DEFAULT_PROPERTIES) + customProperties
    }

    static ExperimentCreationRequest sampleExperimentCreationRequest(Map customProperties = [:]) {
        def properties = sampleExperimentCreationRequestProperties(customProperties)
        ExperimentCreationRequest.builder()
                .id(properties.id as String)
                .variantNames(properties.variantNames as List<String>)
                .percentage(properties.percentage as Integer)
                .internalVariantName(properties.internalVariantName as String)
                .deviceClass(properties.deviceClass as String)
                .description(properties.description as String)
                .documentLink(properties.documentLink as String)
                .groups(properties.groups as List<String>)
                .reportingType(properties.reportingType as ReportingType)
                .customParameterName(properties.customParameterName as String)
                .customParameterValue(properties.customParameterValue as String)
                .eventDefinitions(properties.eventDefinitions as List<EventDefinition>)
                .build()
    }

    static Map withRandomId(Map properties) {
        properties.id = UUID.randomUUID().toString()
        properties
    }
}
