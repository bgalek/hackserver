package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class ExperimentCreationRequestDeserializationSpec extends Specification {

    def "should deserialize ExperimentCreationRequest with ExperimentGoalRequest using Jackson "(){
      given:
      def reqJSON = '''
      {
        "id": "id",
        "variantNames": ["a", "b"],
        "percentage": 1,
        "goal": {
          "leadingMetric": "tx"
        }
      }
      '''

      def jackson = new ObjectMapper()

      when:
      def req = jackson.readValue(reqJSON, ExperimentCreationRequest)

      then:
      req.id == "id"
      req.variantNames == ["a", "b"]
      req.percentage == 1
      req.goal.leadingMetric == "tx"
    }
}
