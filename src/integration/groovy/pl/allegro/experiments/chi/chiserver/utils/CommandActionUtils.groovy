package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentGroupCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.MakeExperimentFullOnProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ProlongExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.UpdateVariantsProperties

trait CommandActionUtils {

    @Autowired
    ExperimentActions experimentActions

    void createExperiment(ExperimentCreationRequest request) {
        experimentActions.create(request)
    }

    void startExperiment(String experimentId, long experimentDurationDays) {
        def properties = new StartExperimentProperties(experimentDurationDays)
        experimentActions.start(experimentId, properties)
    }

    void prolongExperiment(String experimentId, long experimentAdditionalDays) {
        def properties = new ProlongExperimentProperties(experimentAdditionalDays)
        experimentActions.prolong(experimentId, properties)
    }

    void pauseExperiment(String experimentId) {
        experimentActions.pause(experimentId)
    }

    void resumeExperiment(String experimentId) {
        experimentActions.resume(experimentId)
    }

    void makeExperimentFullOn(String experimentId, String fullOnVariantName) {
        def properties = new MakeExperimentFullOnProperties(fullOnVariantName)
        experimentActions.makeExperimentFullOn(experimentId, properties)
    }

    void stopExperiment(String experimentId) {
        experimentActions.stop(experimentId)
    }

    void deleteExperiment(String experimentId) {
        experimentActions.delete(experimentId)
    }

    void createExperimentGroup(List<String> experimentIds, String groupId = UUID.randomUUID().toString()) {
        def request = new ExperimentGroupCreationRequest(groupId, experimentIds)
        experimentActions.createExperimentGroup(request)
    }

    void updateExperimentEventDefinitions(String experimentId, List<EventDefinition> eventDefinitions) {
        experimentActions.updateExperimentEventDefinitions(experimentId, eventDefinitions)
    }

    void updateExperimentVariants(String experimentId,
                                  List<String> variantNames,
                                  String internalVariantName,
                                  int percentage,
                                  String deviceClass) {
        def properties = new UpdateVariantsProperties(variantNames, internalVariantName, percentage, deviceClass)
        experimentActions.updateVariants(experimentId, properties)
    }
}