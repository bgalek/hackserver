package pl.allegro.tech.leaders.hackathon.challenge;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.github.slugify.Slugify;

import java.util.List;
import java.util.Objects;

public interface ChallengeDefinition {

    Slugify slugify = new Slugify();

    String getName();

    String getDescription();

    String getChallengeEndpoint();

    List<QueryParam> getChallengeParams();

    //TODO move away from here closer to the UI
    default JsonSchema getChallengeResponse() {
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(new ObjectMapper());
        try {
            return schemaGen.generateSchema(solutionType());
        } catch (JsonMappingException e) {
            throw new RuntimeException("Could not create schema", e);
        }
    }

    Class<?> solutionType();

    TaskDefinition getExample();

    List<TaskDefinition> getTasks();

    default String getId() {
        return slugify.slugify(getName());
    }

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QueryParam)) {
                return false;
            }
            QueryParam that = (QueryParam) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(desc, that.desc);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, desc);
        }
    }
}
