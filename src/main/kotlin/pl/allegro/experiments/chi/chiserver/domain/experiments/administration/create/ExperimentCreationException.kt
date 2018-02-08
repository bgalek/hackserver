package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create

class ExperimentCreationException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)
