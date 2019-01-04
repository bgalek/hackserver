package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class ExperimentDefinitionBuilder {

    private String id;
    private List<String> variantNames;
    private String internalVariantName;
    private String fullOnVariantName;
    private Integer percentage;
    private DeviceClass deviceClass = DeviceClass.all;
    private String description;
    private String documentLink;
    private String author;
    private List<String> groups = Collections.emptyList();
    private ActivityPeriod activityPeriod;
    private ExperimentStatus explicitStatus;
    private ReportingDefinition reportingDefinition = ReportingDefinition.createDefault();
    private CustomParameter customParameter;
    private ZonedDateTime lastExplicitStatusChange;
    private ExperimentGoal goal;
    private List<ExperimentTag> tags = Collections.emptyList();
    private CustomMetricDefinition customMetricsDefinition;

    private ExperimentDefinitionBuilder() {
    }

    public static ExperimentDefinitionBuilder experimentDefinition() {
        return new ExperimentDefinitionBuilder();
    }

    public ExperimentDefinitionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ExperimentDefinitionBuilder variantNames(List<String> variantNames) {
        this.variantNames = variantNames;
        return this;
    }

    public ExperimentDefinitionBuilder reportingDefinition(ReportingDefinition reportingDefinition) {
        this.reportingDefinition = reportingDefinition;
        return this;
    }

    public ExperimentDefinitionBuilder internalVariantName(String internalVariantName) {
        this.internalVariantName = internalVariantName;
        return this;
    }

    public ExperimentDefinitionBuilder fullOnVariantName(String fullOnVariantName) {
        this.fullOnVariantName = fullOnVariantName;
        return this;
    }

    public ExperimentDefinitionBuilder deviceClass(String deviceClass) {
        this.deviceClass = DeviceClass.fromString(deviceClass);
        return this;
    }

    public ExperimentDefinitionBuilder deviceClass(DeviceClass deviceClass) {
        this.deviceClass = deviceClass;
        return this;
    }

    public ExperimentDefinitionBuilder percentage(Integer percentage) {
        this.percentage = percentage;
        return this;
    }

    public ExperimentDefinitionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ExperimentDefinitionBuilder documentLink(String documentLink) {
        this.documentLink = documentLink;
        return this;
    }

    public ExperimentDefinitionBuilder author(String author) {
        this.author = author;
        return this;
    }

    public ExperimentDefinitionBuilder groups(List<String> groups) {
        this.groups = groups;
        return this;
    }

    public ExperimentDefinitionBuilder activityPeriod(ActivityPeriod activityPeriod) {
        this.activityPeriod = activityPeriod;
        return this;
    }

    public ExperimentDefinitionBuilder activityPeriod(ZonedDateTime activeFrom, ZonedDateTime activeTo) {
        this.activityPeriod = new ActivityPeriod(activeFrom, activeTo);
        return this;
    }

    public ExperimentDefinitionBuilder changeExplicitStatus(ExperimentStatus explicitStatus) {
        this.explicitStatus = explicitStatus;
        lastExplicitStatusChange(ZonedDateTime.now());
        return this;
    }

    public ExperimentDefinitionBuilder explicitStatus(ExperimentStatus explicitStatus) {
        this.explicitStatus = explicitStatus;
        return this;
    }

    public ExperimentDefinitionBuilder lastExplicitStatusChange(ZonedDateTime lastExplicitStatusChange) {
        this.lastExplicitStatusChange = lastExplicitStatusChange.withNano(0).withFixedOffsetZone();
        return this;
    }

    public ExperimentDefinitionBuilder goal(ExperimentGoal goal) {
        this.goal = goal;
        return this;
    }

    public ExperimentDefinitionBuilder goal(ExperimentGoal.Hypothesis hypothesis, ExperimentGoal.TestConfiguration testConfiguration) {
        this.goal = new ExperimentGoal(hypothesis, testConfiguration);
        return this;
    }

    public ExperimentDefinitionBuilder customParameter(CustomParameter customParameter) {
        this.customParameter = customParameter;
        return this;
    }

    public ExperimentDefinitionBuilder tags(List<ExperimentTag> tags) {
        this.tags = tags;
        return this;
    }

    public ExperimentDefinitionBuilder customMetricsDefinition(CustomMetricDefinition cmd) {
        this.customMetricsDefinition = cmd;
        return this;
    }

    public ExperimentDefinition build() {
        return new ExperimentDefinition(
                id,
                variantNames,
                internalVariantName,
                fullOnVariantName,
                percentage,
                deviceClass,
                description,
                documentLink,
                author,
                groups,
                activityPeriod,
                explicitStatus,
                reportingDefinition,
                customParameter,
                lastExplicitStatusChange,
                goal,
                tags,
                customMetricsDefinition);
    }
}
