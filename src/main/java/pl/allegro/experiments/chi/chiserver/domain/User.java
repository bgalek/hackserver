package pl.allegro.experiments.chi.chiserver.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.util.Lists;

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
        this.groups = Lists.sanitizedCopy(groups);
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

        if (isRoot || isAuthor(experiment)) {
            return true;
        }

        var authGroups = Lists.sanitizedCopy(experiment.getGroups());
        return groups.stream().anyMatch(g -> authGroups.contains(g));
    }

    public boolean isAnonymous() {
        return name.equals(ANONYMOUS);
    }

    public boolean isLoggedIn() {
        return !isAnonymous() || isRoot();
    }

    private boolean isAuthor(ExperimentDefinition experiment) {
        return experiment.getAuthor() != null && experiment.getAuthor().equals(name);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", isRoot=" + isRoot +
                ", groups=" + groups +
                '}';
    }
}