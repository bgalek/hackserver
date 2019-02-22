package pl.allegro.tech.leaders.hackathon.challenge.base

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class SampleResponse {
    private final String result

    @JsonCreator
    SampleResponse(
            @JsonProperty("result") String result) {
        this.result = result
    }

    String getResult() {
        return result
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        SampleResponse that = (SampleResponse) o
        if (result != that.result) return false
        return true
    }

    int hashCode() {
        return (result != null ? result.hashCode() : 0)
    }

    @Override
    String toString() {
        return """SampleResponse { "result="${result}" }"""
    }
}
