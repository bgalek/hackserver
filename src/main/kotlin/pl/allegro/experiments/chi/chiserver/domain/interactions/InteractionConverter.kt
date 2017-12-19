package pl.allegro.experiments.chi.chiserver.domain.interactions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.application.interactions.v1.InteractionTypeAdapter
import java.util.ArrayList

class InteractionConverter {
    val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Interaction::class.java, InteractionTypeAdapter())
            .create()

    fun fromJson(json: String): List<Interaction> {
        val listType = object : TypeToken<ArrayList<Interaction>>() {}.getType()
        return gson.fromJson(json, listType)
    }
}