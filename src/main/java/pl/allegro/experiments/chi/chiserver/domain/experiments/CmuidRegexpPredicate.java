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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CmuidRegexpPredicate that = (CmuidRegexpPredicate) o;

        return pattern.equals(that.pattern);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}
