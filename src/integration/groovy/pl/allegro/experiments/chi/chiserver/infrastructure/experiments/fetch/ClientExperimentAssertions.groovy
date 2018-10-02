package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

class ClientExperimentAssertions {

    static void assertShredRange(Map experiment, String variantName, int from, int to, String salt = null) {
        def variant = experiment.variants.find {it.name == variantName}

        assert variant.name == variantName
        assert variant.predicates.size() == 1
        assert variant.predicates[0].type == 'SHRED_HASH'
        assert variant.predicates[0].ranges.size() == 1
        assert variant.predicates[0].ranges[0].from == from
        assert variant.predicates[0].ranges[0].to == to
    }
}
