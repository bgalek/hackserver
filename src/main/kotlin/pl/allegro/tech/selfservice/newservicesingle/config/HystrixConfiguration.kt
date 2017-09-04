package pl.allegro.tech.selfservice.newservicesingle.config

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet
import com.netflix.hystrix.strategy.HystrixPlugins
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.tech.common.andamio.request.hystrix.RequestAwareHystrixConcurrencyStrategy
import pl.allegro.tech.selfservice.newservicesingle.logger
import javax.annotation.PostConstruct

@Configuration
class HystrixConfiguration {

    @Value("\${hystrix.streamUrl}")
    private val streamUrl: String? = null

    @Bean
    fun hystrixCommandAspect() = HystrixCommandAspect()

    @Bean
    fun hystrixStreamServlet() = ServletRegistrationBean(HystrixMetricsStreamServlet(), streamUrl)

    @PostConstruct
    fun registerHystrixConcurrencyStrategy() {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(RequestAwareHystrixConcurrencyStrategy())
        logger.info("Registered RequestAwareHystrixConcurrencyStrategy for Hystrix")
    }

    companion object {
        private val logger by logger()
    }
}