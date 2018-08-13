package pl.allegro.experiments.chi.chiserver.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
        return isRoot || isMemberOfAllowedGroup(experiment) || isAuthor(experiment);
    }

    public boolean isAnonymous() {
        return name.equals(ANONYMOUS);
    }

    public boolean isLoggedIn() {
        return !isAnonymous() || isRoot();
    }

    private boolean isMemberOfAllowedGroup(ExperimentDefinition experiment) {
        return groups.stream().anyMatch(it -> experiment.getGroups().contains(it));
    }

    private boolean isAuthor(ExperimentDefinition experiment) {
        return experiment.getAuthor() != null && experiment.getAuthor().equals(name);
    }
}