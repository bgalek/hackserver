package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
            @JsonProperty("reportingEnabled") Boolean reportingEnabled) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(variantNames);
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


    public Experiment toExperiment(String author) {
        Preconditions.checkNotNull(author);
        try {
            List<ExperimentVariant> experimentVariants = new ArrayList<>();

            if (internalVariantName != null) {
                experimentVariants.add(new ExperimentVariant(internalVariantName, ImmutableList.of(new InternalPredicate())));
            }

            if (percentage != null) {
                int maxPercentageVariant = 100 / variantNames.size();
                if (percentage > maxPercentageVariant) {
                    throw new ExperimentCommandException("Percentage exceeds maximum value ( " + percentage + " > " + maxPercentageVariant + " )");
                }
                for (int i = 0; i < variantNames.size(); i++) {
                    experimentVariants.add(convertVariant(variantNames.get(i), i * maxPercentageVariant,i * maxPercentageVariant + percentage));
                }
            }

            return Experiment.builder()
                    .id(this.id)
                    .variants(experimentVariants)
                    .description(this.description)
                    .documentLink(this.documentLink)
                    .author(author)
                    .groups(this.groups)
                    .reportingEnabled(this.reportingEnabled)
                    .build();

        } catch (Exception e) {
            throw new ExperimentCommandException("Cannot create experiment from request", e);
        }
    }

    private ExperimentVariant convertVariant(String variantName, int from, int to) {

        List<Predicate> predicates = new ArrayList<>();

        if (deviceClass != null) {
            predicates.add(new DeviceClassPredicate(deviceClass));
        }

        predicates.add(new HashRangePredicate(new PercentageRange(from, to)));

        return new ExperimentVariant(
                variantName,
                predicates
        );
    }
}

