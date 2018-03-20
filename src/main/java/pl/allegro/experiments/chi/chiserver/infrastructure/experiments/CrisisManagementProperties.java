package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import java.util.ArrayList;
import java.util.List;

public class CrisisManagementProperties {
    private boolean enabled;
    private List<String> whitelist;

    public CrisisManagementProperties() {
        this.enabled = false;
        this.whitelist = new ArrayList<>();
    }

    public boolean getEnabled() {
        return enabled;
    }

    public List<String> getWhiteList() {
        return whitelist;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setWhitelist(List<String> whiteList) {
        this.whitelist = whiteList;
    }
}
