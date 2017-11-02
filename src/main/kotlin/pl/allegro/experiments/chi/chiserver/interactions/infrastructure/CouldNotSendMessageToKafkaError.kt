package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

class CouldNotSendMessageToKafkaError(message: String): RuntimeException(message) {
}