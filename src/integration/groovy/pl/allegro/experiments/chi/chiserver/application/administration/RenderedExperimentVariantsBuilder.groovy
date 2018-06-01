package pl.allegro.experiments.chi.chiserver.application.administration

import com.google.common.collect.ImmutableList

class RenderedExperimentVariantsBuilder {
    private final List<Variant> variants

    RenderedExperimentVariantsBuilder() {
        this.variants = new ArrayList<>()
    }

    RenderedExperimentVariantsBuilder addVariant(String variantName, String salt, List<Range> ranges) {
        this.variants.add(new Variant(variantName, salt, ranges))
        this
    }

    List build() {
        this.variants.collect { v ->
            [
                    name: v.name,
                    predicates: [
                            [
                                    type: 'SHRED_HASH',
                                    ranges: v.ranges.collect { r ->
                                        [from: r.from, to: r.to]
                                    },
                                    salt: v.salt
                            ]
                    ]
            ]
        }
    }

    static RenderedExperimentVariantsBuilder builder() {
        new RenderedExperimentVariantsBuilder()
    }

    static class Range {
        final int from
        final int to

        Range(int from, int to) {
            this.from = from
            this.to = to
        }

        static Range rangeOf(int from, int to) {
            new Range(from, to)
        }
    }

    private class Variant {
        final String name
        final String salt
        final List<Range> ranges

        Variant(String name, String salt, List<Range> ranges) {
            this.name = name
            this.salt = salt
            this.ranges = ImmutableList.copyOf(ranges)
        }
    }
}
