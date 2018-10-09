package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;

import java.lang.reflect.Type;

public class AdminExperimentTypeSerializer implements JsonSerializer<AdminExperiment> {
    @Override
    public JsonElement serialize(AdminExperiment src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", src.getId());

        jsonObject.add("renderedVariants", context.serialize(src.getRenderedVariants()));

        jsonObject.add("variantNames", context.serialize(src.getVariantNames()));
        jsonObject.addProperty("internalVariantName", src.getInternalVariantName().orElse(null));
        jsonObject.addProperty("deviceClass", src.getDeviceClass().orElse(null));
        jsonObject.addProperty("percentage", src.getPercentage().orElse(null));
        jsonObject.addProperty("description", src.getDescription());
        jsonObject.addProperty("documentLink", src.getDocumentLink());
        jsonObject.addProperty("author", src.getAuthor());
        jsonObject.add("groups", context.serialize(src.getGroups()));
        if (src.getBonferroniCorrection() != 0) {
            jsonObject.add("bonferroniCorrection", context.serialize(src.getBonferroniCorrection()));
        }
        jsonObject.addProperty("reportingEnabled", true);
        jsonObject.add("activityPeriod", context.serialize(src.getActivityPeriod()));
        jsonObject.add("editable", context.serialize(src.getEditable()));
        jsonObject.add("status", context.serialize(src.getStatus()));
        jsonObject.add("maxPossibleAllocation", context.serialize(src.getMaxPossibleAllocation()));
        jsonObject.add("reportingType", context.serialize(src.getReportingType()));
        jsonObject.add("eventDefinitions", context.serialize(src.getEventDefinitions()));
        jsonObject.add("measurements", context.serialize(src.getMeasurements()));
        if (src.getBayesianHorizontalEqualizer() != null) {
            jsonObject.add("bayesianEqualizer", context.serialize(src.getBayesianHorizontalEqualizer()));
        }
        if (src.getExperimentGroup() != null) {
            jsonObject.add("experimentGroup", serializeExperimentGroup(src.getExperimentGroup(), context));
        }
        if (src.getLastStatusChange() != null) {
            jsonObject.add("lastStatusChange", context.serialize(src.getLastStatusChange()));
        }
        return jsonObject;
    }

    private JsonObject serializeExperimentGroup(ExperimentGroup group, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", group.getId());
        jsonObject.add("experiments", context.serialize(group.getExperiments()));
        jsonObject.addProperty("salt", group.getSalt());

        return jsonObject;
    }
}

