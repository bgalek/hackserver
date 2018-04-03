package pl.allegro.experiments.chi.chiserver.application.experiments;

import com.google.common.base.Preconditions;
import java.util.List;

import static org.javers.common.collections.Lists.asList;

/**
 * TODO this is a POC
 */
class DummyFiltersProvider {

    List<ExperimentFilterDefinition> provide() {
        return asList(
            new ExperimentFilterDefinition("test-dev", "", "boxView", "zażółć gęślą jaźń", ""),
            new ExperimentFilterDefinition("test-ulubionej-kategorii-na-stronie-gwnej", "", "boxView", "reco__carousel", ""),
            new ExperimentFilterDefinition("everything-up-to-10", "", "boxView", "Nowe USP B images desktop xlarge", "allegro.image.tiles"),
            new ExperimentFilterDefinition("everything-up-to-10", "", "boxView", "Nowe USP B images desktop small", "allegro.image.tiles"),
            new ExperimentFilterDefinition("everything-up-to-10", "", "boxView", "Nowe USP B images desktop medium", "allegro.image.tiles"),
            new ExperimentFilterDefinition("everything-up-to-10", "", "boxView", "Nowe USP B images desktop large", "allegro.image.tiles")
            );
    }

    public static class ExperimentFilterDefinition {
        private final String experimentId;
        private final String eventValue;
        private final String eventAction;
        private final String eventLabel;
        private final String eventCategory;

        public ExperimentFilterDefinition(String experimentId, String eventValue, String eventAction, String eventLabel, String eventCategory) {
            Preconditions.checkArgument(experimentId != null);
            this.experimentId = experimentId;
            this.eventValue = nullToEmpty(eventValue);
            this.eventAction = nullToEmpty(eventAction);
            this.eventLabel = nullToEmpty(eventLabel);
            this.eventCategory = nullToEmpty(eventCategory);
        }

        public String getExperimentId() {
            return this.experimentId;
        }

        public String getEventValue() {
            return this.eventValue;
        }

        public String getEventAction() {
            return this.eventAction;
        }

        public String getEventLabel() {
            return this.eventLabel;
        }

        public String getEventCategory() {
            return this.eventCategory;
        }
    }

    static String nullToEmpty(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}