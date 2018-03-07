package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExperimentTypeDeserializer implements JsonDeserializer<Experiment> {

    @Override
    public Experiment deserialize(
            JsonElement jsonElement,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        JsonObject json = jsonElement.getAsJsonObject();

        String id = json.get("id").getAsString();

        JsonElement rawDescription = json.get("description");
        JsonElement rawDocumentLink = json.get("rawDocumentLink");
        String description = rawDescription != null ? rawDescription.getAsString(): null;
        String documentLink = rawDocumentLink != null ? rawDocumentLink.getAsString(): null;

        JsonElement rawOwner = json.get("owner");
        String owner = rawOwner != null ? rawOwner.getAsString(): null;


        JsonElement rawReported = json.get("reportingEnabled");
        boolean reported = rawReported == null || rawReported.getAsBoolean();

        List<ExperimentVariant> variants = deserializeVariants(json, context);

        ExperimentMeasurements measurements = json.get("measurements") != null ? deserializeExperimentMeasurements(json, context): null;

        JsonElement rawExperimentStatus = json.get("explicitStatus");
        ExperimentStatus experimentStatus = rawExperimentStatus != null ? ExperimentStatus.valueOf(rawExperimentStatus.getAsString()): null;

        JsonElement rawActiveFrom = json.get("activeFrom");
        JsonElement rawActiveTo = json.get("activeTo");
        ZonedDateTime activeFrom = rawActiveFrom != null ? ZonedDateTime.parse(rawActiveFrom.getAsString(), formatter): null;
        ZonedDateTime activeTo = rawActiveTo != null ? ZonedDateTime.parse(rawActiveTo.getAsString(), formatter): null;
        ActivityPeriod activityPeriod = activeFrom != null && activeTo != null ? new ActivityPeriod(activeFrom, activeTo): null;

        return Experiment.builder()
                .id(id)
                .variants(variants)
                .description(description)
                .documentLink(documentLink)
                .author(owner)
                .groups(new ArrayList<>())
                .reportingEnabled(reported)
                .activityPeriod(activityPeriod)
                .measurements(measurements)
                .explicitStatus(experimentStatus)
                .build();
    }

    private List<ExperimentVariant> deserializeVariants(JsonObject json, JsonDeserializationContext context) {
        Type listOfVariantsType = new TypeToken<ArrayList<ExperimentVariant>>(){}.getType();
        return context.deserialize(json.get("variants"), listOfVariantsType);
    }

    private ExperimentMeasurements deserializeExperimentMeasurements(JsonObject json, JsonDeserializationContext context) {
        Type measurementsType = new TypeToken<ExperimentMeasurements>(){}.getType();
        return context.deserialize(json.get("measurements"), measurementsType);
    }
}
