package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.tech.common.andamio.spring.client.ClientConnectionConfig

data class ClientConnectionProperties(
        val maxConnections: Int = 200,
        val maxConnectionsPerRoute: Int = 10,
        val connectionTimeout: Int = 200,
        val socketTimeout: Int = 300,
        val name: String? = null) {

    fun toConfig(): ClientConnectionConfig {
        return ClientConnectionConfig.clientConnectionConfig()
                .apply {
                    withConnectionTimeout(connectionTimeout)
                    withMaxConnections(maxConnections)
                    withMaxConnectionsPerRoute(maxConnectionsPerRoute)
                    withSocketTimeout(socketTimeout)
                    name?.let {
                        withName(name)
                    }
                }.build()
    }
}