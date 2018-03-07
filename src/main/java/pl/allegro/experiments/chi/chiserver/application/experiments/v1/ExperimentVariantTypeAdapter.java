package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.*;
import org.springframework.web.bind.annotation.PathVariable;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ExperimentVariantTypeAdapter implements JsonSerializer<ExperimentVariant>, JsonDeserializer<ExperimentVariant> {
    private static final String CMUID_REGEXP_TYPE = "CMUID_REGEXP";
    private static final String HASH_TYPE = "HASH";
    private static final String INTERNAL_TYPE = "INTERNAL";
    private static final String DEVICE_CLASS_TYPE = "DEVICE_CLASS";

    @Override
    public JsonElement serialize(ExperimentVariant src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();
        element.addProperty("name", src.getName());
        element.add("predicates", context.serialize(src.getPredicates()));
        return element;
    }

    @Override
    public ExperimentVariant deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();
        String id = json.get("name").getAsString();
        return new ExperimentVariant(id, deserializePredicates(json));
    }

    private List<Predicate> deserializePredicates(JsonObject json) {
        Iterator<JsonElement> predicates = json.get("predicates").getAsJsonArray().iterator();
        ArrayList<Predicate> result = new ArrayList<>();
        while (predicates.hasNext()) {
            result.add(this.deserializePredicate((JsonObject) predicates.next()));
        }
        return result;
    }

    private Predicate deserializePredicate(JsonObject json) {
        String type = json.get("type").getAsString();
        if (DEVICE_CLASS_TYPE.equals(type)) {
            return deserializeDevicePredicate(json);
        } else if (HASH_TYPE.equals(type)) {
            return deserializeHashPredicate(json);
        } else if (CMUID_REGEXP_TYPE.equals(type)) {
            return deserializeCmuidPredicate(json);
        } else if (INTERNAL_TYPE.equals(type)) {
            return deserializeInternalVariant(json);
        }

        throw new IllegalArgumentException("Cannot handle ExperimentInteraction type : " + type);
    }

    private Predicate deserializeHashPredicate(JsonObject json) {
        int from = json.get("from").getAsInt();
        int to = json.get("to").getAsInt();
        return new HashRangePredicate(new PercentageRange(from, to));
    }

    private Predicate deserializeInternalVariant(JsonObject json) {
        return new InternalPredicate();
    }

    private Predicate deserializeCmuidPredicate(JsonObject json) {
        String regexp = json.get("regexp").getAsString();
        return new CmuidRegexpPredicate(Pattern.compile(regexp));
    }

    private Predicate deserializeDevicePredicate(JsonObject json) {
        String device = json.get("device").getAsString();
        return new DeviceClassPredicate(device);
    }
}
