package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

class ClientExperimentAssertions {

    static void assertShredRange(Map experiment, String variantName, int from, int to, String salt) {
        def variant = experiment.variants.find {it.name == variantName}
        assert variant == [
                name      : variantName,
                predicates: [
                        [
                                type  : 'SHRED_HASH',
                                ranges: [[from: from, to: to]],
                                salt  : salt
                        ]
                ]
        ]
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
