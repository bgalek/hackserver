package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.Samples;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class VariantBayesianStatisticsDeserializer implements JsonDeserializer<VariantBayesianStatistics> {
    @Override
    public VariantBayesianStatistics deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String variantName = json.get("variantName").getAsString();

        JsonObject samplesJson = json.get("samples").getAsJsonObject();
        List<Double> values = context.deserialize(samplesJson.get("values"), new TypeToken<List<Double>>(){}.getType());
        List<Integer> counts = context.deserialize(samplesJson.get("counts"), new TypeToken<List<Integer>>(){}.getType());
        Samples samples = new Samples(values, counts, Collections.emptyList());

        int outliersLeft = json.get("outliersLeft").getAsInt();
        int outliersRight = json.get("outliersRight").getAsInt();

        return new VariantBayesianStatistics(variantName, samples, outliersLeft, outliersRight);
    }
}