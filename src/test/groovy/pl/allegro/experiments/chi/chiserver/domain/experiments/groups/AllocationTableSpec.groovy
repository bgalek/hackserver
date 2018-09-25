package pl.allegro.experiments.chi.chiserver.domain.experiments.groups

import spock.lang.Specification
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationRecord.forBase
import static pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationRecord.forVariant

class AllocationTableSpec extends Specification {

    def "should detect range clash"(){
      when:
      new AllocationTable([
                     forVariant('e', 'b', 10, 20),
                     forVariant('b', 'c', 20, 21),
                     forVariant('b', 'c', 21, 30),
                     forVariant('d', 'e', 29, 30)
              ])

      then:
      def e = thrown(IllegalArgumentException)
      println e.getMessage()
    }

    def "should block exclusive base allocation"(){
        when:
        new AllocationTable([
                    forVariant('e', 'base', 10, 20)
                ])

        then:
        def e = thrown(IllegalArgumentException)
        println e.getMessage()
    }


    def "should sum allocation for base and non-base"(){
        when:
        def table = new AllocationTable([
                forBase(10, 20),
                forVariant('b', 'c',    20, 21),
                forBase(22, 30),
                forVariant('d', 'e',    30, 31)
        ])

        then:
        table.baseAllocation == 18
        table.nonBaseAllocation == 2
    }

    @Unroll
    def "should check if there is more space to allocate for #numberOfVariants variants, and #morePercent more percent"(){
        given:
        def table = new AllocationTable([
              forBase(0, 10),
              forBase(50, 60),
              forVariant('exp1', 'v1', 10, 30),
              forVariant('exp1', 'v2', 60, 65),
              forVariant('exp1', 'v2', 70, 75),
              forVariant('exp1', 'v2', 80, 90)
        ])
        assert table.baseAllocation == 20
        assert table.nonBaseAllocation == 40

        when:
        def check = table.checkAllocation(numberOfVariants, morePercent)

        then:
        check == possible

        where:
        numberOfVariants | morePercent | possible
        3                | 10          | true
        3                | 13          | true
        3                | 14          | false
        3                | 15          | false
        2                | 20          | true
        2                | 21          | false
    }

    def "should prevent from allocating more than 100"(){
        given:
        def table = new AllocationTable([
                forBase(50, 80),
                forVariant('exp1', 'v1', 0, 30),
        ])

        when:
        table.allocate("exp1", ["v1","base"], 30)

        then:
        def e = thrown(IllegalArgumentException)
        println e.getMessage()
    }

    def "should prevent from allocating without base"(){
        given:
        def table = new AllocationTable([
                forBase(50, 80),
                forVariant('exp1', 'v1', 0, 30),
        ])

        when:
        table.allocate("exp1", ["v1"], 30)

        then:
        def e = thrown(IllegalArgumentException)
        println e.getMessage()
    }

    def "should allocate free space"(){
        given:
        AllocationTable table = new AllocationTable([])

        when:
        table = table.allocate("exp1", ["v1","v2", "base"], 5)

        then:
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forBase(95, 100)

        when:
        table = table.allocate("exp1", ["v1", "v2", "base"], 10)

        then:
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forVariant("exp1", "v1",  10, 15)
        table.records[3] == forVariant("exp1", "v2",  15, 20)
        table.records[4] == forBase(90, 100)

        when:
        table = table.allocate("exp2", ["v1", "base"], 10)

        then:
        table.baseAllocation == 10
        table.nonBaseAllocation == 30
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forVariant("exp1", "v1",  10, 15)
        table.records[3] == forVariant("exp1", "v2",  15, 20)
        table.records[4] == forVariant("exp2", "v1",  20, 30)
        table.records[5] == forBase(90, 100)

    }
}