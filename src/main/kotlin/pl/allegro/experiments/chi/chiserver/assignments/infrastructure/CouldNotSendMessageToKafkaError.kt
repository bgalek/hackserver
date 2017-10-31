package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

class CouldNotSendMessageToKafkaError(message: String): RuntimeException(message) {
}