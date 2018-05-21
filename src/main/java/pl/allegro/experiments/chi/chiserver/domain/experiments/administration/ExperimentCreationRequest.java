package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperimentCreationRequest {
    @NotNull
    private final String id;
    @NotNull
    private final List<String> variantNames;
    private final String internalVariantName;
    private final Integer percentage;
    private final String deviceClass;
    private final String description;
    private final String documentLink;
    private final List<String> groups;
    private final boolean reportingEnabled;
    private final ReportingDefinition reportingDefinition;

    @JsonCreator
    public ExperimentCreationRequest(
            @JsonProperty("id") String id,
            @JsonProperty("variantNames") List<String> variantNames,
            @JsonProperty("internalVariantName") String internalVariantName,
            @JsonProperty("percentage") Integer percentage,
            @JsonProperty("deviceClass") String deviceClass,
            @JsonProperty("description") String description,
            @JsonProperty("documentLink") String documentLink,
            @JsonProperty("groups") List<String> groups,
            @JsonProperty("reportingEnabled") Boolean reportingEnabled,
            @JsonProperty("eventDefinitions") List<EventDefinition> eventDefinitions,
            @JsonProperty("reportingType") ReportingType reportingType) {
        Preconditions.checkArgument(id != null, "experiment id is null");
        Preconditions.checkArgument(variantNames != null, "experiment variantNames are null");
        Preconditions.checkArgument(percentage != null, "experiment percentage is null");
        Preconditions.checkArgument(percentage >= 0, "experiment percentage < 0");
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
        this.description = description;
        this.documentLink = documentLink;
        if (groups == null) {
            this.groups = ImmutableList.copyOf(new ArrayList<>());
        } else {
            this.groups = ImmutableList.copyOf(groups);
        }
        if (reportingEnabled == null) {
            this.reportingEnabled = false;
        } else {
            this.reportingEnabled = reportingEnabled;
        }
        this.reportingDefinition = Optional.ofNullable(reportingType)
                .map(rt -> rt.reportingDefinition(eventDefinitions))
                .orElse(ReportingDefinition.createDefault());

    }

    public String getId() {
        return id;
    }

    public List<String> getVariantNames() {
        return variantNames;
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean getReportingEnabled() {
        return reportingEnabled;
    }

    public ExperimentDefinition toExperimentDefinition(String author) {
        Preconditions.checkNotNull(author);
        try {
            return ExperimentDefinition.builder()
                    .id(this.id)
                    .variantNames(variantNames)
                    .internalVariantName(internalVariantName)
                    .percentage(percentage)
                    .deviceClass(deviceClass)
                    .description(this.description)
                    .documentLink(this.documentLink)
                    .author(author)
                    .groups(this.groups)
                    .reportingEnabled(this.reportingEnabled)
                    .reportingDefinition(this.reportingDefinition)
                    .build();

        } catch (Exception e) {
            throw new ExperimentCommandException("Cannot create experiment from request", e);
        }
    }
}

