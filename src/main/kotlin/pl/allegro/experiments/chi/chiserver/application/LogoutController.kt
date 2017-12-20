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
    fun getUserInfo(request: HttpServletRequest): RedirectView {
        val baseUrl = getBaseUrl(request)
        val redirect = String.format("%s?client_id=%s&redirect_uri=%s", logoutUrl, clientId, baseUrl)
        return RedirectView(redirect)
    }

    private fun getBaseUrl(request: HttpServletRequest): String {
        val baseUrl: String
        baseUrl = if (request.serverPort == 80) {
            String.format("%s://%s/", request.scheme, request.serverName)
        } else {
            String.format("%s://%s:%d/", request.scheme, request.serverName, request.serverPort)
        }
        return baseUrl
    }

}