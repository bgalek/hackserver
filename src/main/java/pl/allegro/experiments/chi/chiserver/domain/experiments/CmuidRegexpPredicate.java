package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

public class CmuidRegexpPredicate implements Predicate {
    private final Pattern pattern;

    public CmuidRegexpPredicate(Pattern pattern) {
        Preconditions.checkNotNull(pattern);
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

}
