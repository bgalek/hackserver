package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop

class StopExperimentException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)
