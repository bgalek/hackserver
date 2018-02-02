package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider

@Component
class SpringSecurityUserProvider(@Value("\${security.rootGroup}") val rootGroupName: String,
                                 @Value("\${security.enabled}") val securityEnabled: Boolean): UserProvider {

    override fun getCurrentUser(): User {
        return createUser(SecurityContextHolder.getContext().authentication, rootGroupName)
    }

    private fun createUser(authentication: Authentication?, rootGroupName: String): User {
        if (authentication == null || authentication is AnonymousAuthenticationToken) {
            return User("Anonymous", emptyList(), !securityEnabled)
        }

        val list:List<String> = authentication.authorities.map { it.authority }.toList()
        return User(authentication.name, list, list.contains(rootGroupName))
    }
}
