package pl.allegro.experiments.chi.chiserver.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(value = "/after-logout")
class LogoutController {

    @Value(value = "\${security.oauth2.client.logoutUri}")
    private val logoutUrl: String? = null

    @Value(value = "\${security.oauth2.client.clientId}")
    private val clientId: String? = null

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun afterLogout(request: HttpServletRequest): RedirectView {
        val baseUrl = getBaseUrl(request)
        return RedirectView("$logoutUrl?client_id=$clientId&redirect_uri=$baseUrl")
    }

    private fun getBaseUrl(request: HttpServletRequest): String {
        return if (request.serverPort == 80) {
            "${request.scheme}://${request.serverName}/"
        } else {
            "${request.scheme}://${request.serverName}:${request.serverPort}/"
        }
    }

}