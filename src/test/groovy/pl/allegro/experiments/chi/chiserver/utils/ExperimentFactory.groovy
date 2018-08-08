package pl.allegro.experiments.chi.chiserver.utils

import groovy.transform.CompileStatic
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.ExperimentVariantTypeAdapter
import pl.allegro.experiments.chi.chiserver.domain.experiments.CmuidRegexpPredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange
import pl.allegro.experiments.chi.chiserver.domain.experiments.Predicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition

import java.util.regex.Pattern
import java.util.stream.Collectors

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

    static ExperimentDefinition backendReportingDefinition(String id) {
        ExperimentDefinition.builder()
            .id(id)
            .variantNames(variants().collect{it -> it.getName()})
            .groups([])
            .reportingDefinition(ReportingDefinition.createDefault())
            .build()
    }

    static List<ExperimentVariant> variants() {
        [
                new ExperimentVariant("base", []),
                new ExperimentVariant("v2", [])
        ]
    }

    static Experiment experimentWithId(String id) {
        List<ExperimentVariant> variants = variants()
        return experimentWithVariants(id, variants)
    }

    static ExperimentDefinition definitionWithId(String id) {
        return ExperimentDefinition.builder()
                .id(id)
                .variantNames(['base', 'v2'])
                .percentage(10)
                .description('description')
                .documentLink('link')
                .author('owner')
                .groups([])
                .build()
    }

   static Experiment experimentWithVariants(String id, List<ExperimentVariant> variants) {
        return Experiment.builder()
                .id(id)
                .variants(variants)
                .description("description")
                .documentLink("link")
                .author("owner")
                .groups([])
                .build()
    }
}
