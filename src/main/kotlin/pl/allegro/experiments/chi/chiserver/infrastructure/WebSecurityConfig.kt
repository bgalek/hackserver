package pl.allegro.experiments.chi.chiserver.infrastructure

import org.apache.http.auth.BasicUserPrincipal
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.header.writers.StaticHeadersWriter

@Configuration
@EnableOAuth2Sso
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${security.enabled}")
    private val oauthEnabled: Boolean = false

    @Value(value = "\${security.rootGroup}")
    private val rootGroup: String? = null

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        http.headers().cacheControl().disable()
        http.csrf().disable()
        http.headers().addHeaderWriter(StaticHeadersWriter(HttpHeaders.CACHE_CONTROL, "private"))

        if (oauthEnabled) {
            http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/**/*", "/explicitStatus/**/*", "/env/**/*").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/**/*").permitAll()
                    .and().authorizeRequests().anyRequest().authenticated()
                    .and().logout().logoutSuccessUrl("/after-logout").permitAll()
        } else {
            http.anonymous()
                    .principal(BasicUserPrincipal("admin"))
                    .authorities(rootGroup)
                    .and().authorizeRequests().anyRequest().permitAll()
        }
    }
}
