package pl.allegro.experiments.chi.chiserver.domain.interactions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import pl.allegro.experiments.chi.chiserver.application.interactions.v1.InteractionTypeAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InteractionConverter {
    private final Gson gson;

    public InteractionConverter() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Interaction.class, new InteractionTypeAdapter())
                .create();
    }

    public List<Interaction> fromJson(String json) {
        Type listType = (new TypeToken<List<Interaction>>() {}).getType();
        Object result = this.gson.fromJson(json, listType);
        return result == null ? new ArrayList<Interaction>(): (List)result;
    }
}