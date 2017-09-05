package pl.allegro.experiments.chi.chiserver.domain

// TODO consider creating range object as in client
class HashRangeExperimentVariant(name: String, val from: Int, val to: Int) : ExperimentVariant(name, "HASH")
