package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomMetricDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentGoal;
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
        jsonObject.add("tags", context.serialize(src.getTags()));
        jsonObject.addProperty("reportingEnabled", true);
        jsonObject.add("activityPeriod", context.serialize(src.getActivityPeriod()));
        jsonObject.add("editable", context.serialize(src.getEditable()));
        jsonObject.add("status", context.serialize(src.getStatus()));
        jsonObject.add("maxPossibleAllocation", context.serialize(src.getMaxPossibleAllocation()));
        jsonObject.add("reportingType", context.serialize(src.getReportingType()));
        jsonObject.add("eventDefinitions", context.serialize(src.getEventDefinitions()));
        if (src.getBayesianHorizontalEqualizer() != null) {
            jsonObject.add("bayesianEqualizer", context.serialize(src.getBayesianHorizontalEqualizer()));
        }
        if (src.getExperimentGroup() != null) {
            jsonObject.add("experimentGroup", serializeExperimentGroup(src.getExperimentGroup(), context));
        }
        if (src.getLastStatusChange() != null) {
            jsonObject.add("lastStatusChange", context.serialize(src.getLastStatusChange()));
        }
//        src.getCustomMetricsDefinition().ifPresent(cmd -> jsonObject.add("customMetricDefinition", serializeCustomMetricDefinition(cmd, context)));
        src.getGoal().ifPresent(goal -> jsonObject.add("goal", serializeExperimentGoal(goal, context)));
        return jsonObject;
    }


    private JsonObject serializeCustomMetricDefinition(CustomMetricDefinition cmd, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", cmd.getName());
        jsonObject.add("successEventDefinition", context.serialize(cmd.getSuccessEventDefinition()));
        jsonObject.add("viewEventDefinition", context.serialize(cmd.getViewEventDefinition()));
        return jsonObject;

    }

    private JsonObject serializeExperimentGoal(ExperimentGoal goal, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("leadingMetric", goal.getHypothesis().getLeadingMetric());
        jsonObject.addProperty("expectedDiffPercent", goal.getHypothesis().getExpectedDiffPercent());

        jsonObject.addProperty("leadingMetricBaselineValue", goal.getTestConfiguration().getLeadingMetricBaselineValue());
        jsonObject.addProperty("testAlpha", goal.getTestConfiguration().getTestAlpha());
        jsonObject.addProperty("testPower", goal.getTestConfiguration().getTestPower());
        jsonObject.addProperty("requiredSampleSize", goal.getTestConfiguration().getRequiredSampleSize());
        jsonObject.addProperty("currentSampleSize", goal.getTestConfiguration().getCurrentSampleSize());

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

