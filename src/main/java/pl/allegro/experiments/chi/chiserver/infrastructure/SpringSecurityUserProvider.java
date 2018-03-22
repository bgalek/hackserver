package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SpringSecurityUserProvider implements UserProvider {
    private final String rootGroupName;
    private final boolean securityEnabled;

    public SpringSecurityUserProvider(
            @Value("${security.rootGroup}") String rootGroupName,
            @Value("${security.enabled}") boolean securityEnabled) {
        this.rootGroupName = rootGroupName;
        this.securityEnabled = securityEnabled;
    }

    @Override
    public User getCurrentUser() {
        return createUser(SecurityContextHolder.getContext().getAuthentication(), rootGroupName);
    }

    private User createUser(Authentication authentication, String rootGroupName) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new User("Anonymous", Collections.emptyList(), !securityEnabled);
        }

        List<String> list = authentication.getAuthorities().stream()
                .map(it -> it.getAuthority())
                .collect(Collectors.toList());
        return new User(authentication.getName(), list, list.contains(rootGroupName));
    }
}
