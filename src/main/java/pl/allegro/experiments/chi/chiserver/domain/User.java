package pl.allegro.experiments.chi.chiserver.domain;

import com.google.common.base.Preconditions;
import jdk.packager.services.userjvmoptions.PreferencesUserJvmOptions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

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
        this.groups = groups;
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

    public boolean isOwner(Experiment experiment) {
        Preconditions.checkNotNull(experiment);
        return isRoot || groups.stream().anyMatch(g -> experiment.getGroups().contains(g)) || isAuthor(experiment);
    }

    public boolean isAnonymous() {
        return name.equals(ANONYMOUS);
    }

    private boolean isAuthor(Experiment experiment) {
        return experiment.getAuthor() != null && experiment.getAuthor().equals(name);
    }
}