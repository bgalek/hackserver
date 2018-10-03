package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange

class ClientExperimentAssertions {

    static void assertShredRange(Map experiment, String variantName, List<PercentageRange> ranges,  String salt) {
        def variant = experiment.variants.find {it.name == variantName}

        assert variant.name == variantName
        assert variant.predicates.size() == 1
        assert variant.predicates[0].type == 'SHRED_HASH'
        assert variant.predicates[0].salt == salt
        assert variant.predicates[0].ranges.size() == ranges.size()

        (0..ranges.size()-1).each {
            assert variant.predicates[0].ranges[it] == [from: ranges[it].from, to:  ranges[it].to]
        }
    }

    static void assertShredRange(Map experiment, String variantName, int from, int to, String salt) {
        assertShredRange(experiment, variantName, [new PercentageRange(from, to)], salt)
    }

    static void  assertHashRange(Map experiment, String variantName, int from, int to) {
        def variant = experiment.variants.find {it.name == variantName}
        assert variant == [
                name      : variantName,
                predicates: [
                        [
                                type  : 'HASH',
                                from  : from,
                                to    : to
                        ]
                ]
        ]
    }
}
