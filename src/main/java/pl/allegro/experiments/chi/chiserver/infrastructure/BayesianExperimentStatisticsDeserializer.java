package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;

import java.lang.reflect.Type;
import java.util.List;

public class BayesianExperimentStatisticsDeserializer implements JsonDeserializer<BayesianExperimentStatistics> {

    @Override
    public BayesianExperimentStatistics deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String id = json.get("experimentId").getAsString();
        String toDate = json.get("toDate").getAsString();
        String deviceClass = json.get("device").getAsString();

        return new BayesianExperimentStatistics(id, toDate, deviceClass, deserializeVariants(json, context));
    }

    private List<VariantBayesianStatistics> deserializeVariants(JsonObject json, JsonDeserializationContext context) {
        Type listOfVariantsType = new TypeToken<List<VariantBayesianStatistics>>(){}.getType();
        return context.deserialize(json.get("variantBayesianStatistics"), listOfVariantsType);
    }
}