package pl.allegro.experiments.chi.chiserver.interactions.v1

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import java.util.ArrayList

class InteractionConverter {
    var gson: Gson = GsonBuilder()
            .registerTypeAdapter(Interaction::class.java, InteractionTypeAdapter())
            .create()

    fun fromJson(json: String): List<Interaction> {
        try {
            val listType = object : TypeToken<ArrayList<Interaction>>() {}.getType()
            return gson.fromJson(json, listType)
        } catch (e: Exception) { // rozdrobnic?
            throw InvalidFormatException("Cant deserialize Interaction. Invalid format.")
        }
    }
}