package pl.allegro.experiments.chi.chiserver.utils

import groovy.transform.CompileStatic
import pl.allegro.experiments.chi.chiserver.domain.experiments.CmuidRegexpPredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange
import pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate

import java.util.regex.Pattern

@CompileStatic
class ExperimentFactory {

    static Experiment simple50to50(String id, String vA, String vB) {
        List<ExperimentVariant> variants = [
                new ExperimentVariant(vA, [new HashRangePredicate(new PercentageRange(0, 50)) as Predicate]),
                new ExperimentVariant(vB, [new HashRangePredicate(new PercentageRange(50, 100)) as Predicate])]
        return experimentWithVariants(id, variants)
    }

    static Experiment simple50(String id, String vA) {
        List<ExperimentVariant> variants = [new ExperimentVariant(vA, [new HashRangePredicate(new PercentageRange(0, 50)) as Predicate])]
        return experimentWithVariants(id, variants)
    }

    static Experiment regexp50to50(String id, String vA, String vB) {
        List<ExperimentVariant> variants = [
                new ExperimentVariant(vA, [new CmuidRegexpPredicate(Pattern.compile('.*[0-7]$'))as Predicate]),
                new ExperimentVariant(vB, [new CmuidRegexpPredicate(Pattern.compile('.*[8-9,a-f]$'))as Predicate])
        ]
        return experimentWithVariants(id, variants)
    }

    static Experiment experimentWithId(String id) {
        List<ExperimentVariant> variants = [
                new ExperimentVariant("base", []),
                new ExperimentVariant("v2", []),
        ]
        return experimentWithVariants(id, variants)
    }

   static Experiment experimentWithVariants(String id, List<ExperimentVariant> variants) {
        return Experiment.builder()
                .id(id)
                .variants(variants)
                .description("description")
                .documentLink("link")
                .author("owner")
                .reportingEnabled(true)
                .groups([])
                .build()
    }
}
