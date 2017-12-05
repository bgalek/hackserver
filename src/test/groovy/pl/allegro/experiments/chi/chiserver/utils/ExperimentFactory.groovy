package pl.allegro.experiments.chi.chiserver.utils;

import pl.allegro.experiments.chi.chiserver.domain.*

import java.util.regex.Pattern;

class ExperimentFactory {

    static Experiment simple50to50(String id, String vA, String vB) {
        return new Experiment(id, [
                new ExperimentVariant(vA, [new HashRangePredicate(new PercentageRange(0, 50))]),
                new ExperimentVariant(vB, [new HashRangePredicate(new PercentageRange(50, 100))])],
                "desc", "owner", true, null, null)
    }

    static Experiment simple50(String id, String vA) {
        return new Experiment(id, [ new ExperimentVariant(vA, [new HashRangePredicate(new PercentageRange(0, 50))])], "desc", "owner", true, null, null)
    }

    static Experiment regexp50to50(String id, String vA, String vB) {
        return new Experiment(id, [
                                    new ExperimentVariant(vA, [new CmuidRegexpPredicate(Pattern.compile('.*[0-7]$'))]),
                                    new ExperimentVariant(vB, [new CmuidRegexpPredicate(Pattern.compile('.*[8-9,a-f]$'))])
                                  ],
                                "description", "owner", true, null, null
                             )
    }
}
