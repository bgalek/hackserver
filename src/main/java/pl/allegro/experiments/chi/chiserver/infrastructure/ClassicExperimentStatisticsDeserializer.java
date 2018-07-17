package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

class ClassicExperimentStatisticsDeserializer implements JsonDeserializer<ClassicExperimentStatistics> {
    private static final Type mapType = new TypeToken<Map<String, Map<String, VariantStatistics>>>() {}.getType();

    @Override
    public ClassicExperimentStatistics deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String id = json.get("experimentId").getAsString();
        LocalDate toDate = context.deserialize(json.get("toDate"), LocalDate.class);
        Duration duration = context.deserialize(json.get("duration"), Duration.class);
        DeviceClass device = DeviceClass.fromString(json.get("device").getAsString());
        Map<String, Map<String, VariantStatistics>> metrics = context.deserialize(json.get("metrics"), mapType);

        return new ClassicExperimentStatistics(id, toDate, duration, device, metrics);
    }
}