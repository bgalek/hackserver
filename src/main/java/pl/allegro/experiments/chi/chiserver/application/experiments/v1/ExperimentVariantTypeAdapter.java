package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ExperimentVariantTypeAdapter implements JsonSerializer<ExperimentVariant>, JsonDeserializer<ExperimentVariant> {
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
        if (PredicateType.DEVICE_CLASS.toString().equals(type)) {
            return deserializeDevicePredicate(json);
        } else if (PredicateType.HASH.toString().equals(type)) {
            return deserializeHashPredicate(json);
        } else if (PredicateType.INTERNAL.toString().equals(type)) {
            return deserializeInternalVariant(json);
        } else if (PredicateType.CUSTOM_PARAM.toString().equals(type)) {
            return deserializeCustomParameterPredicate(json);
        } else if (PredicateType.FULL_ON.toString().equals(type)) {
            return deserializeFullOnPredicate(json);
        }

        throw new IllegalArgumentException("Cannot handle ExperimentInteraction type : " + type);
    }

    private Predicate deserializeCustomParameterPredicate(JsonObject json) {
        String name = json.get("name").getAsString();
        String value = json.get("value").getAsString();
        return new CustomParameterPredicate(name, value);
    }

    private Predicate deserializeHashPredicate(JsonObject json) {
        int from = json.get("from").getAsInt();
        int to = json.get("to").getAsInt();
        return new HashRangePredicate(new PercentageRange(from, to));
    }

    private Predicate deserializeInternalVariant(JsonObject json) {
        return new InternalPredicate();
    }

    private Predicate deserializeDevicePredicate(JsonObject json) {
        String device = json.get("device").getAsString();
        return new DeviceClassPredicate(device);
    }

    private Predicate deserializeFullOnPredicate(JsonObject json) {
        return new FullOnPredicate();
    }
}
