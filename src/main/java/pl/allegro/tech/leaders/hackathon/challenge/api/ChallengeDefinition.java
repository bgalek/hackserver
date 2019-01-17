package pl.allegro.tech.leaders.hackathon.challenge.api;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.github.slugify.Slugify;

import java.util.List;
import java.util.Objects;

public interface ChallengeDefinition {

    Slugify slugify = new Slugify();

    String getName();

    String getDescription();

    String getChallengeEndpoint();

    List<QueryParam> getChallengeParams();

    JsonSchema getChallengeResponse();

    List<String> getExamples();

    List<ChallengeTask> getTasks();

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
            if (o == null || getClass() != o.getClass()) return false;
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
