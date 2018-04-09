package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.util.Optional;

public class EventDefinition {
    private final String category;
    private final String action;
    private final String value;
    private final String label;

    public EventDefinition(String category, String action, String value, String label) {
        this.category = "".equals(category) ? null : category;
        this.action = "".equals(action) ? null : action;
        this.value = "".equals(value) ? null : value;
        this.label = "".equals(label) ? null : label;
    }

    public Optional<String> getCategory() {
        return Optional.ofNullable(category);
    }

    public Optional<String> getAction() {
        return Optional.ofNullable(action);
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }
}
