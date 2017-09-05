package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.slf4j.LoggerFactory

class WireMockRuleForIntegration extends WireMockRule {

    private static ThreadLocal<Integer> currentPort = new ThreadLocal<>();

    WireMockRuleForIntegration(int port) {
        super(port);
    }

    @Override
    protected void before() {
        super.before();
        LoggerFactory.getLogger(getClass()).warn("Current port: " + port());
        currentPort.set(port());
    }

    static int getCurrentPort() {
        return currentPort.get();
    }
}
