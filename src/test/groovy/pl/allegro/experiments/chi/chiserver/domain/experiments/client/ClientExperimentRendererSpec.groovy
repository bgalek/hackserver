package pl.allegro.experiments.chi.chiserver.domain.experiments.client

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import spock.lang.Specification

class ClientExperimentRendererSpec extends Specification {

    def "should trim rendered ranges when variant allocation is greater then target"(){
        given:
        def exp1 = ExperimentDefinitionBuilder.experimentDefinition()
                .id("exp1").variantNames(["v1", "base"])
                .percentage(5)
                .build()
        def group = new ExperimentGroup("groupId", "salt", ["exp1"],
                new AllocationTable([]).allocate ('exp1', ['v1', 'base'], 4)
                                       .allocate ('exp2', ['v1', 'base'], 10)
                                       .allocate ('exp1', ['v1', 'base'], 10))


        when:
        def clientExp = new ClientExperimentRenderer(exp1, group).render()

        then:
        group.getAllocationSumFor(exp1.id, "v1") == 10
        group.getAllocationSumFor(exp1.id, "base") == 10

        clientExp.getVariants()[0].name == "v1"
        clientExp.getVariants()[0].predicates[0].ranges[0] == new PercentageRange( 0,  4)
        clientExp.getVariants()[0].predicates[0].ranges[1] == new PercentageRange(14, 15)
        clientExp.getVariants()[1].name == "base"
        clientExp.getVariants()[1].predicates[0].ranges[0] == new PercentageRange(96,  100)
        clientExp.getVariants()[1].predicates[0].ranges[1] == new PercentageRange(85,  86)
    }

    def "should trim rendered base when shared base allocation is greater then target"(){
        given:
        def exp1 = ExperimentDefinitionBuilder.experimentDefinition()
                .id("exp1").variantNames(["v1", "base"])
                .percentage(10)
                .build()

        def group = new ExperimentGroup("groupId", "salt", ["exp1"],
                new AllocationTable([]).allocate ('exp1', ['v1', 'base'], 20, true))


        when:
        def clientExp = new ClientExperimentRenderer(exp1, group).render()

        then:
        group.getSharedBaseAllocationSum() == 20

        clientExp.getVariants()[0].name == "v1"
        clientExp.getVariants()[0].predicates[0].ranges[0] == new PercentageRange(0, 10)
        clientExp.getVariants()[1].name == "base"
        clientExp.getVariants()[1].predicates[0].ranges[0] == new PercentageRange(90, 100)
    }
}
