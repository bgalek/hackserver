package pl.allegro.experiments.chi.chiserver.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import pl.allegro.tech.auth.oauthresourcesecurity.singleauthorization.SingleAuthorizationResourceServerConfigurer;

@Configuration
@ConditionalOnProperty("security.enabled")
@EnableWebSecurity
@EnableResourceServer
public class ResourceServerConfiguration extends SingleAuthorizationResourceServerConfigurer {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/admin/**", "/api/bayes/**")
                .authenticated()
            .anyRequest()
                .permitAll();
    }
}
