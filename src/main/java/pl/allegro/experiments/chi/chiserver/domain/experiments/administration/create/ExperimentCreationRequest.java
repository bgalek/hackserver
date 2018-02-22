package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExperimentCreationRequest {
    private String id;
    private List<Variant> variants;
    private String description;
    private String documentLink;
    private List<String> groups;
    private boolean reportingEnabled;

    public ExperimentCreationRequest() {}

    public ExperimentCreationRequest(
            String id,
            List<Variant> variants,
            String description,
            String documentLink,
            List<String> groups,
            Boolean reportingEnabled) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(variants);
        this.id = id;
        this.variants = variants;
        this.description = description;
        this.documentLink = documentLink;
        if (groups == null) {
            this.groups = new ArrayList<>();
        } else {
            this.groups = groups;
        }
        if (reportingEnabled == null) {
            this.reportingEnabled = false;
        } else {
            this.reportingEnabled = reportingEnabled;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void setReportingEnabled(boolean reportingEnabled) {
        this.reportingEnabled = reportingEnabled;
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
        private String name;
        private List<Predicate> predicates;

        public Variant() {}

        public Variant(String name, List<Predicate> predicates) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(predicates);
            this.name = name;
            this.predicates = predicates;
        }

        public String getName() {
            return name;
        }

        public List<Predicate> getPredicates() {
            return predicates;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPredicates(List<Predicate> predicates) {
            this.predicates = predicates;
        }
    }

    public enum PredicateType {
        INTERNAL, HASH, CMUID_REGEXP, DEVICE_CLASS
    }

    public static class Predicate {
        private PredicateType type;
        private Integer from;
        private Integer to;
        private String regexp;
        private String device;

        public Predicate() {}

        public Predicate(
                PredicateType type,
                Integer from,
                Integer to,
                String regexp,
                String device) {
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

        public void setType(PredicateType type) {
            this.type = type;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public void setRegexp(String regexp) {
            this.regexp = regexp;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }
}
