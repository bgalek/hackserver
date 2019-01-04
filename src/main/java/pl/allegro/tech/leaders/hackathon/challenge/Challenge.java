package pl.allegro.tech.leaders.hackathon.challenge;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.github.slugify.Slugify;

import java.util.List;

public interface Challenge {

    Slugify slugify = new Slugify();

    default String getId() {
        return slugify.slugify(getName());
    }

    String getName();

    String getDescription();

    String getChallengeEndpoint();

    List<QueryParam> getChallengeParams();

    JsonSchema getChallengeResponse() throws JsonMappingException;

    List<String> getExamples();

    List<Task> getTasks();

    class QueryParam {
        private final String name;
        private final String desc;

        public QueryParam(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
