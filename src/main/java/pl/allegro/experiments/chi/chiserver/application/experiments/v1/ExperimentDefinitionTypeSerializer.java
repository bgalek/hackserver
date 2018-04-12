package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.lang.reflect.Type;

public class ExperimentDefinitionTypeSerializer implements JsonSerializer<ExperimentDefinition> {

    @Override
    public JsonElement serialize(
            ExperimentDefinition experimentDefinition,
            Type typeOfSrc,
            JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", experimentDefinition.getId());
        jsonObject.add("variantNames", context.serialize(experimentDefinition.getVariantNames()));
        jsonObject.addProperty("internalVariantName", experimentDefinition.getInternalVariantName().orElse(null));
        jsonObject.addProperty("deviceClass", experimentDefinition.getDeviceClass().orElse(null));
        jsonObject.addProperty("percentage", experimentDefinition.getPercentage().orElse(null));
        jsonObject.addProperty("description", experimentDefinition.getDescription());
        jsonObject.addProperty("documentLink", experimentDefinition.getDocumentLink());
        jsonObject.addProperty("author", experimentDefinition.getAuthor());
        jsonObject.add("groups", context.serialize(experimentDefinition.getGroups()));
        jsonObject.addProperty("reportingEnabled", experimentDefinition.getReportingEnabled());
        jsonObject.add("activityPeriod", context.serialize(experimentDefinition.getActivityPeriod()));
        jsonObject.add("editable", context.serialize(experimentDefinition.getEditable()));
        jsonObject.add("status", context.serialize(experimentDefinition.getStatus()));

        return jsonObject;
    }
}
