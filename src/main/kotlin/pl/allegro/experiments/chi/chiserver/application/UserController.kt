package pl.allegro.experiments.chi.chiserver.application

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider

@RestController
@RequestMapping(value = ["/api/admin/user"])
class UserController(val userProvider: UserProvider) {

    @RequestMapping(value = [""])
    fun user(): ResponseEntity<User> {
        return ResponseEntity.ok(userProvider.getCurrentUser())
    }
}
