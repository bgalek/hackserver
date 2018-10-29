package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics;

import java.lang.reflect.Type;

class ClassicExperimentStatisticsForVariantMetricDeserializer implements JsonDeserializer<ClassicExperimentStatisticsForVariantMetric> {

    @Override
    public ClassicExperimentStatisticsForVariantMetric deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String id = json.get("experimentId").getAsString();
        String toDate = json.get("toDate").getAsString();
        DeviceClass deviceClass = DeviceClass.fromString(json.get("device").getAsString());
        String variantName = json.get("variantName").getAsString();
        String metricName = json.get("metricName").getAsString();
        VariantStatistics data = context.deserialize(json.getAsJsonObject("data"), VariantStatistics.class);

        return new ClassicExperimentStatisticsForVariantMetric(id, toDate, deviceClass, variantName, metricName, data);
    }
}