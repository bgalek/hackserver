package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.delete

class DeleteExperimentException (override val message: String, override val cause : Exception? = null): RuntimeException(message, cause)