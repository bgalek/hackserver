package pl.allegro.experiments.chi.chiserver.infrastructure.security;

import org.apache.http.auth.BasicUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import pl.allegro.tech.auth.oauthresourcesecurity.singleauthorization.SingleAuthorizationResourceServerConfigurer;

@Configuration
@EnableWebSecurity
@EnableResourceServer
public class ResourceServerConfiguration extends SingleAuthorizationResourceServerConfigurer {
    private final boolean securityEnabled;
    private final String rootGroup;

    @Autowired
    ResourceServerConfiguration(
            @Value("${security.enabled}") boolean securityEnabled,
            @Value("${security.rootGroup}") String rootGroup) {
        this.securityEnabled = securityEnabled;
        this.rootGroup = rootGroup;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (securityEnabled) {
            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/admin/**", "/api/bayes/**")
                        .authenticated()
                    .anyRequest()
                        .permitAll();
        }
        else {
            http.anonymous()
                    .principal(new BasicUserPrincipal("admin"))
                    .authorities(rootGroup)
                    .and()
                    .authorizeRequests().anyRequest().permitAll();
        }
    }
}
