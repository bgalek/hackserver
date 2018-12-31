package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentGoal.TestConfiguration;
import pl.allegro.experiments.chi.chiserver.util.BigDecimals;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition;
import static pl.allegro.experiments.chi.chiserver.util.BigDecimals.round2;
import static pl.allegro.experiments.chi.chiserver.util.BigDecimals.round4;

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
    private final ReportingDefinition reportingDefinition;
    private final CustomParameter customParameter;
    private final ExperimentGoalRequest goal;
    private final List<String> tags;
    private final CustomMetricDefinition customMetricDefinition;

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
            @JsonProperty("eventDefinitions") List<EventDefinition> eventDefinitions,
            @JsonProperty("reportingType") ReportingType reportingType,
            @JsonProperty("customParameterName") String customParameterName,
            @JsonProperty("customParameterValue") String customParameterValue,
            @JsonProperty("goal") ExperimentGoalRequest goal,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("customMetricDefinition") CustomMetricDefinition customMetricDefinition) {
        Preconditions.checkArgument(id != null, "experiment id is null");
        Preconditions.checkArgument(variantNames != null, "experiment variantNames are null");
        Preconditions.checkArgument(percentage != null, "experiment percentage is null");
        Preconditions.checkArgument(percentage >= 0, "experiment percentage < 0");
        if (StringUtils.isNotBlank(customParameterName) || StringUtils.isNotBlank(customParameterValue)) {
            Preconditions.checkArgument(StringUtils.isNotBlank(customParameterName), "custom parameter name is blank");
            Preconditions.checkArgument(StringUtils.isNotBlank(customParameterValue), "custom parameter value is blank");
        }
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
        this.description = description;
        this.documentLink = documentLink;
        this.tags = ImmutableList.copyOf(tags == null ? new ArrayList<>() : tags);
        if (groups == null) {
            this.groups = ImmutableList.copyOf(new ArrayList<>());
        } else {
            this.groups = ImmutableList.copyOf(groups);
        }
        this.reportingDefinition = Optional.ofNullable(reportingType)
                .map(rt -> rt.reportingDefinition(eventDefinitions))
                .orElse(ReportingDefinition.createDefault());

        if (StringUtils.isNotBlank(customParameterName)) {
            this.customParameter = new CustomParameter(customParameterName.trim(), customParameterValue.trim());
        } else {
            this.customParameter = null;
        }
        this.goal = goal;
        this.customMetricDefinition = customMetricDefinition;


    }

    public List<String> getTags() {
        return tags;
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

    public ExperimentGoalRequest getGoal() {
        return goal;
    }

    public CustomMetricDefinition getCustomMetric () {
        return customMetricDefinition;
    }

    ExperimentDefinition toExperimentDefinition(String author) {
        Preconditions.checkNotNull(author);
        try {
            var builder =  experimentDefinition()
                    .id(this.id)
                    .variantNames(variantNames)
                    .internalVariantName(internalVariantName)
                    .percentage(percentage)
                    .deviceClass(deviceClass)
                    .description(this.description)
                    .documentLink(this.documentLink)
                    .author(author)
                    .groups(this.groups)
                    .reportingDefinition(this.reportingDefinition)
                    .customParameter(this.customParameter)
                    .tags(this.tags.stream().map(it -> new ExperimentTag(it)).collect(Collectors.toList()))
                    .customMetricDefinition(this.customMetricDefinition);

            if (goal != null && StringUtils.isNotBlank(goal.getLeadingMetric())) {
                var hypothesis = new ExperimentGoal.Hypothesis(goal.getLeadingMetric(), round2(goal.getExpectedDiffPercent()));

                TestConfiguration config = null;
                if (goal.hasNonEmptyTestConfiguration() && isValid(goal)) {
                    config = new TestConfiguration(
                            round2(goal.getLeadingMetricBaselineValue()),
                            round2(goal.getTestAlpha()),
                            round2(goal.getTestPower()),
                            goal.getRequiredSampleSize(),
                            0);
                }
                builder.goal(hypothesis, config);
            }
            return builder.build();
        } catch (Exception e) {
            throw new ExperimentCommandException("Cannot create experiment from request", e);
        }
    }

    private boolean isValid(ExperimentGoalRequest goal) {
        if (round4(goal.getTestAlpha()).compareTo(round2(0.01)) < 0) {
            throw new ExperimentCommandException("Invalid experiment.goal.testAlpha: " + goal.getTestAlpha() + ", it should be >= 0.01");
        }

        if (round4(goal.getTestPower()).compareTo(round2(0.8)) < 0) {
            throw new ExperimentCommandException("Invalid experiment.goal.testPower: " + goal.getTestPower() + ", it should be >= 0.8");
        }

        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    static public class Builder {

        private String id;
        private List<String> variantNames;
        private String internalVariantName;
        private Integer percentage;
        private String deviceClass;
        private String description;
        private String documentLink;
        private List<String> groups;
        private List<EventDefinition> eventDefinitions;
        private ReportingType reportingType;
        private String customParameterName;
        private String customParameterValue;
        private List<String> tags;
        private CustomMetricDefinition customMetricDefinition;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder variantNames(List<String> variantNames) {
            this.variantNames = variantNames;
            return this;
        }

        public Builder internalVariantName(String internalVariantName) {
            this.internalVariantName = internalVariantName;
            return this;
        }

        public Builder percentage(Integer percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder deviceClass(String deviceClass) {
            this.deviceClass = deviceClass;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder documentLink(String documentLink) {
            this.documentLink = documentLink;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder groups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder eventDefinitions(List<EventDefinition> eventDefinitions) {
            this.eventDefinitions = eventDefinitions;
            return this;
        }

        public Builder reportingType(ReportingType reportingType) {
            this.reportingType = reportingType;
            return this;
        }

        public Builder customParameterName(String customParameterName) {
            this.customParameterName = customParameterName;
            return this;
        }

        public Builder customParameterValue(String customParameterValue) {
            this.customParameterValue = customParameterValue;
            return this;
        }

        public Builder customMetricDefinition(CustomMetricDefinition customMetricDefinition) {
            this.customMetricDefinition = customMetricDefinition;
            return this;
        }

        public ExperimentCreationRequest build() {
            return new ExperimentCreationRequest(
                    id,
                    variantNames,
                    internalVariantName,
                    percentage,
                    deviceClass,
                    description,
                    documentLink,
                    groups,
                    eventDefinitions,
                    reportingType,
                    customParameterName,
                    customParameterValue,
                    null,
                    tags,
                    customMetricDefinition
                    );
        }
    }
}

