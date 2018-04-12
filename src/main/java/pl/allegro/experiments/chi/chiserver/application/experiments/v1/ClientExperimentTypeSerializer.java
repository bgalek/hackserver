package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperiment;

import java.lang.reflect.Type;

public class ClientExperimentTypeSerializer implements JsonSerializer<ClientExperiment> {

    @Override
    public JsonElement serialize(
            ClientExperiment experiment,
            Type typeOfSrc,
            JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", experiment.getId());
        jsonObject.add("variants", context.serialize(experiment.getVariants()));
        jsonObject.addProperty("reportingEnabled", experiment.isReportingEnabled());
        jsonObject.add("activityPeriod", context.serialize(experiment.getActivityPeriod()));
        jsonObject.add("status", context.serialize(experiment.getStatus()));
        return jsonObject;
    }
}
