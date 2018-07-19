package pl.allegro.experiments.chi.chiserver.domain.experiments;

import org.javers.core.metamodel.annotation.DiffInclude;

public class CustomParameter {

    private final String name;
    private final String value;

    public CustomParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @DiffInclude
    public String getName() {
        return name;
    }

    @DiffInclude
    public String getValue() {
        return value;
    }
}
