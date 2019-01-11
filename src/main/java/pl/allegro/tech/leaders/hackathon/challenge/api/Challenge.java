package pl.allegro.tech.leaders.hackathon.challenge.api;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.github.slugify.Slugify;

import java.util.List;

public interface Challenge {

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

    default ChallengeDetails toChallengeDetailsDto() {
        return new ChallengeDetails(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getChallengeEndpoint(),
                this.getChallengeParams(),
                this.getChallengeResponse(),
                this.getExamples()
        );
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
    }
}
