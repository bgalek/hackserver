package pl.allegro.experiments.chi.chiserver.domain.experiments;

public class CustomParameterPredicate implements Predicate {
    private final String name;
    private final String value;

    public CustomParameterPredicate(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
