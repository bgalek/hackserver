package pl.allegro.experiments.chi.chiserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
@ComponentScan
class AppRunner {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(AppRunner::class.java, *args)
        }
    }
}
