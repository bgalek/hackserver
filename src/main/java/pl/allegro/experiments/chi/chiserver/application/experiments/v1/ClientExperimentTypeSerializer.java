package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;
import pl.allegro.experiments.chi.chiserver.domain.experiments.FullOnPredicate;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ClientExperimentTypeSerializer implements JsonSerializer<ClientExperiment> {

    @Override
    public JsonElement serialize(
            ClientExperiment experiment,
            Type typeOfSrc,
            JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", experiment.getId());
        jsonObject.addProperty("reportingEnabled", experiment.isReportingEnabled());
        jsonObject.add("status", context.serialize(experiment.getStatus()));


        if (experiment.getStatus() == ExperimentStatus.FULL_ON) {
            List<ExperimentVariant> fullOnVariants = experiment.getVariants().stream()
                    .filter(it -> it.getPredicates().stream()
                            .anyMatch(predicate -> predicate instanceof FullOnPredicate))
                    .collect(toList());
            jsonObject.add("variants", context.serialize(fullOnVariants));
        } else {
            jsonObject.add("activityPeriod", context.serialize(experiment.getActivityPeriod()));
            jsonObject.add("variants", context.serialize(experiment.getVariants()));
        }

        return jsonObject;
    }
}
