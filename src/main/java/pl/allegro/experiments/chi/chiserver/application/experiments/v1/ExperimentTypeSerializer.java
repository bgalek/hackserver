package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;

import java.lang.reflect.Type;

public class ExperimentTypeSerializer implements JsonSerializer<Experiment> {

    @Override
    public JsonElement serialize(
            Experiment experiment,
            Type typeOfSrc,
            JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", experiment.getId());
        jsonObject.add("variants", context.serialize(experiment.getVariants()));
        jsonObject.addProperty("description", experiment.getDescription());
        jsonObject.addProperty("documentLink", experiment.getDocumentLink());
        jsonObject.addProperty("author", experiment.getAuthor());
        jsonObject.add("groups", context.serialize(experiment.getGroups()));
        jsonObject.addProperty("reportingEnabled", experiment.getReportingEnabled());
        jsonObject.add("activityPeriod", context.serialize(experiment.getActivityPeriod()));
        jsonObject.add("measurements", context.serialize(experiment.getMeasurements()));
        jsonObject.add("editable", context.serialize(experiment.getEditable()));
        jsonObject.add("status", context.serialize(experiment.getStatus()));
        jsonObject.add("origin", context.serialize(experiment.getOrigin()));

        return jsonObject;
    }
}
