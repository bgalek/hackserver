package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.tech.common.andamio.spring.client.ClientConnectionConfig

data class ClientConnectionProperties(
        var maxConnections: Int = 200,
        var maxConnectionsPerRoute: Int = 10,
        var connectionTimeout: Int = 200,
        var socketTimeout: Int = 300,
        var name: String? = null) {

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