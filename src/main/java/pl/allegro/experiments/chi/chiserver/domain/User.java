package pl.allegro.experiments.chi.chiserver.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.util.List;

public class User {
    private static final String ANONYMOUS = "Anonymous";
    private final String name;
    private final List<String> groups;
    private final boolean isRoot;

    public User(String name, List<String> groups, boolean isRoot) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(groups);
        this.name = name;
        this.groups = ImmutableList.copyOf(groups);
        this.isRoot = isRoot;
    }

    public String getName() {
        return name;
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public boolean isOwner(ExperimentDefinition experiment) {
        Preconditions.checkNotNull(experiment);
        return isRoot || groups.stream().anyMatch(g -> experiment.getGroups().contains(g)) || isAuthor(experiment);
    }

    public boolean isAnonymous() {
        return name.equals(ANONYMOUS);
    }

    private boolean isAuthor(ExperimentDefinition experiment) {
        return experiment.getAuthor() != null && experiment.getAuthor().equals(name);
    }
}