package pl.allegro.experiments.chi.chiserver.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/after-logout")
public class LogoutController {
    private final String logoutUrl;
    private final String clientId;

    LogoutController(
            @Value(value = "${security.oauth2.client.logoutUri}") String logoutUrl,
            @Value(value = "${security.oauth2.client.clientId}") String clientId) {
        this.logoutUrl = logoutUrl;
        this.clientId = clientId;
    }

    @GetMapping
    RedirectView afterLogout(HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        return new RedirectView(logoutUrl + "?client_id=" + clientId + "&redirect_uri=" + baseUrl);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80) {
            return baseUrl + ":" + request.getServerPort();
        }
        return baseUrl + "/";
    }
}