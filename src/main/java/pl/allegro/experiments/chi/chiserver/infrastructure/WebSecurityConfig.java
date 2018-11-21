package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.apache.http.auth.BasicUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableOAuth2Sso
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final boolean oauthEnabled;
    private final String rootGroup;

    WebSecurityConfig(
            @Value("${security.enabled}") Boolean oauthEnabled,
            @Value(value = "${security.rootGroup}") String rootGroup) {
        this.oauthEnabled = oauthEnabled != null ? oauthEnabled : false;
        this.rootGroup = rootGroup;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().cacheControl().disable();
        http.csrf().disable();
        http.headers().addHeaderWriter(new StaticHeadersWriter(HttpHeaders.CACHE_CONTROL, "private"));

        if (oauthEnabled) {
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/**/*", "/status/**/*", "/explicitStatus/**/*", "/env/**/*").permitAll()
                    .antMatchers("/login**","/callback/").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/**/*").permitAll()
                    .and().authorizeRequests().anyRequest().authenticated()
                    .and().logout().logoutSuccessUrl("/after-logout").permitAll();
        } else {
            http.anonymous()
                    .principal(new BasicUserPrincipal("admin"))
                    .authorities(rootGroup)
                    .and()
                    .authorizeRequests().anyRequest().permitAll();
        }
    }
}
