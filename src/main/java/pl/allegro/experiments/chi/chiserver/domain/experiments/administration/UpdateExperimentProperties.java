package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class UpdateExperimentProperties {
    private final String description;
    private final String documentLink;
    private final List<String> groups;
    private final List<String> tags;

    @JsonCreator
    public UpdateExperimentProperties(
            @JsonProperty("description") String description,
            @JsonProperty("documentLink") String documentLink,
            @JsonProperty("tags") List<String> tags,
            @JsonProperty("groups") List<String> groups) {
        this.description = description;
        this.documentLink = documentLink;
        this.groups = groups == null ? Collections.emptyList() : groups;
        this.tags = tags == null ? Collections.emptyList() : tags;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDocumentLink() {
        return this.documentLink;
    }

    public List<String> getGroups() {
        return this.groups;
    }

    public List<String> getTags() {
        return tags;
    }
}
