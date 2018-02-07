package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

class StartExperimentException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)
