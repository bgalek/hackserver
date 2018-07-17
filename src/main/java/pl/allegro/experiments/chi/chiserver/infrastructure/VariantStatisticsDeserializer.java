package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics;

import java.lang.reflect.Type;

class VariantStatisticsDeserializer implements JsonDeserializer<VariantStatistics> {

    @Override
    public VariantStatistics deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();

        double value = json.get("value").getAsDouble();
        double diff = json.get("diff").getAsDouble();
        double pValue = json.get("pValue").getAsDouble();
        int count = json.get("count").getAsInt();

        return new VariantStatistics(value, diff, pValue, count);
    }
}