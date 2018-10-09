package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;

import java.lang.reflect.Type;

public class ExperimentGroupTypeSerializer implements JsonSerializer<ExperimentGroup> {

    @Override
    public JsonElement serialize(ExperimentGroup src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.addProperty("id", src.getId());
        result.addProperty("salt", src.getSalt());
        result.add("experiments", context.serialize(src.getExperiments()));
        result.add("allocationTable", context.serialize(src.getAllocationTable().getRecords()));
        return result;
    }
}
