package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.Samples;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;

import java.lang.reflect.Type;
import java.util.List;

public class BayesianExperimentStatisticsDeserializer implements JsonDeserializer<BayesianExperimentStatisticsForVariant> {

    @Override
    public BayesianExperimentStatisticsForVariant deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var json = jsonElement.getAsJsonObject();
        var id = json.get("experimentId").getAsString();
        var toDate = json.get("toDate").getAsString();
        var deviceClass = json.get("device").getAsString();
        var variantName = json.get("variantName").getAsString();

        var dataNode = json.getAsJsonObject("data");

        return new BayesianExperimentStatisticsForVariant(id, toDate, deviceClass, variantName, deserializeVariantData(dataNode, variantName, context));
    }

    private VariantBayesianStatistics deserializeVariantData(JsonObject json, String variantName, JsonDeserializationContext context) {

        JsonObject samplesJson = json.get("samples").getAsJsonObject();
        List<Double> values = context.deserialize(samplesJson.get("values"), new TypeToken<List<Double>>(){}.getType());
        List<Integer> counts = context.deserialize(samplesJson.get("counts"), new TypeToken<List<Integer>>(){}.getType());
        Samples samples = new Samples(values, counts);

        int outliersLeft = json.get("outliersLeft").getAsInt();
        int outliersRight = json.get("outliersRight").getAsInt();

        return new VariantBayesianStatistics(variantName, samples, outliersLeft, outliersRight);
    }
}