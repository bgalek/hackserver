package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong

class ProlongExperimentException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)