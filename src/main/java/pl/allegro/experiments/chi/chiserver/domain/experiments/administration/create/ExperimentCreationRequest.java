package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CmuidRegexpPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClassPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.InternalPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExperimentCreationRequest {
    @NotNull
    private final String id;
    @NotNull
    private final List<Variant> variants;
    private final String description;
    private final String documentLink;
    private final List<String> groups;
    private final boolean reportingEnabled;

    @JsonCreator
    public ExperimentCreationRequest(
            @JsonProperty("id") String id,
            @JsonProperty("variants") List<Variant> variants,
            @JsonProperty("description") String description,
            @JsonProperty("documentLink") String documentLink,
            @JsonProperty("groups") List<String> groups,
            @JsonProperty("reportingEnabled") Boolean reportingEnabled) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(variants);
        this.id = id;
        this.variants = ImmutableList.copyOf(variants);
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

    public List<Variant> getVariants() {
        return variants;
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
            return new Experiment(
                    this.id,
                    this.variants.stream().map(v -> convertVariant(v)).collect(Collectors.toList()),
                    this.description,
                    this.documentLink,
                    author,
                    this.groups,
                    this.reportingEnabled,
                    null,
                    null,
                    null,
                     null,
                    null
            );
        } catch (Exception e) {
            throw new ExperimentCreationException("Cannot create experiment from request", e);
        }
    }

    private ExperimentVariant convertVariant(Variant variant) {
        return new ExperimentVariant(
                variant.name,
                variant.predicates.stream().map(p -> convertPredicate(p)).collect(Collectors.toList())
        );
    }

    private pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate convertPredicate(Predicate predicate) {
        if (predicate.type == PredicateType.INTERNAL) {
            return new InternalPredicate();
        } else if (predicate.type == PredicateType.CMUID_REGEXP) {
            return new CmuidRegexpPredicate(Pattern.compile(predicate.regexp));
        } else if (predicate.type == PredicateType.HASH) {
            return new HashRangePredicate(new PercentageRange(predicate.from, predicate.to));
        } else if (predicate.type == PredicateType.DEVICE_CLASS) {
            return new DeviceClassPredicate(predicate.device);
        } else {
            throw new RuntimeException("Unknown predicate");
        }
    }

    public static class Variant {
        @NotNull
        private String name;
        @NotNull
        private List<Predicate> predicates;

        @JsonCreator
        public Variant(
                @JsonProperty("name") String name,
                @JsonProperty("predicates") List<Predicate> predicates) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(predicates);
            this.name = name;
            this.predicates = ImmutableList.copyOf(predicates);
        }

        public String getName() {
            return name;
        }

        public List<Predicate> getPredicates() {
            return predicates;
        }
    }

    public enum PredicateType {
        INTERNAL, HASH, CMUID_REGEXP, DEVICE_CLASS
    }

    public static class Predicate {
        @NotNull
        private PredicateType type;
        private Integer from;
        private Integer to;
        private String regexp;
        private String device;

        @JsonCreator
        public Predicate(
                @JsonProperty("type") PredicateType type,
                @JsonProperty("from") Integer from,
                @JsonProperty("to") Integer to,
                @JsonProperty("regexp") String regexp,
                @JsonProperty("device") String device) {
            Preconditions.checkNotNull(type);
            this.type = type;
            this.from = from;
            this.to = to;
            this.regexp = regexp;
            this.device = device;
        }

        public PredicateType getType() {
            return type;
        }

        public Integer getFrom() {
            return from;
        }

        public Integer getTo() {
            return to;
        }

        public String getRegexp() {
            return regexp;
        }

        public String getDevice() {
            return device;
        }
    }
}
