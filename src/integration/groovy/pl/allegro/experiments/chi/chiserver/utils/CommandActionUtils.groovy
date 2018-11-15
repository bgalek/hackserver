package pl.allegro.experiments.chi.chiserver.utils

import org.springframework.beans.factory.annotation.Autowired
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentActions
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.AddExperimentToGroupRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentTagCreationRequest
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

    String createExperimentTag(String tagId = null) {
        if (tagId == null) {
            tagId = UUID.randomUUID().toString()
        }
        experimentActions.createExperimentTag(new ExperimentTagCreationRequest(tagId))
        tagId
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

    String createExperimentGroup(List<String> experimentIds, String groupId = UUID.randomUUID().toString()) {
        experimentIds.each {
            experimentActions.addExperimentToGroup(new AddExperimentToGroupRequest(groupId, it))
        }
        groupId
    }

    void updateExperimentEventDefinitions(String experimentId, List<EventDefinition> eventDefinitions) {
        experimentActions.updateExperimentEventDefinitions(experimentId, eventDefinitions)
    }

    void updateExperimentVariants(String experimentId,
                                  String internalVariantName,
                                  int percentage,
                                  String deviceClass) {
        def properties = new UpdateVariantsProperties(internalVariantName, percentage, deviceClass)
        experimentActions.updateVariants(experimentId, properties)
    }
}