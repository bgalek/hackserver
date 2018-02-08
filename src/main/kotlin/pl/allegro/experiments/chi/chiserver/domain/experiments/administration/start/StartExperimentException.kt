package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start

class StartExperimentException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)
