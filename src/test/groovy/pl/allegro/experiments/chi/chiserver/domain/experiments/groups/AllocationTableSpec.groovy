package pl.allegro.experiments.chi.chiserver.domain.experiments.groups

import spock.lang.Specification
import spock.lang.Unroll

import static pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationRecord.forSharedBase
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

    def "should sum allocation for base and non-base"(){
        when:
        def table = new AllocationTable([
                forSharedBase(10, 20),
                forVariant('b', 'c',    20, 21),
                forSharedBase(22, 30),
                forVariant('d', 'e',    30, 31)
        ])

        then:
        table.sharedBaseAllocationSum == 18
        table.nonBaseAllocationSum == 2
    }

    @Unroll
    def "should check if can allocate #percentage percent to experiment #experimentId when sharedBase is #sharedBase"(){
        given:
        def table = new AllocationTable([
                forSharedBase(0, 10),
                forSharedBase(50, 60),
                forVariant('exp1', 'v1', 10, 30), // 20
                forVariant('exp1', 'v2', 60, 65), //  5
                forVariant('exp1', 'v2', 70, 75), //  5
                forVariant('exp1', 'v2', 80, 90)  // 10
        ])
        assert table.sharedBaseAllocationSum == 20
        assert table.nonBaseAllocationSum == 40

        expect:
        table.checkAllocation(experimentId, ['v1','v2','base'], percentage, sharedBase) == possible

        where:
        experimentId  |  sharedBase  | percentage  || possible
        'exp1'        |  true        | 33          || true
        'exp1'        |  true        | 34          || false
        'exp1'        |  false       | 26          || true
        'exp1'        |  false       | 27          || false
        'exp2'        |  true        | 20          || true
        'exp2'        |  true        | 21          || false
        'exp2'        |  false       | 13          || true
        'exp2'        |  false       | 14          || false
    }

    @Unroll
    def "should prevent from allocating more than 100"(){
        given:
        def table = new AllocationTable([
                forSharedBase(50, 80),            //30
                forVariant('exp1', 'v1', 0, 30),  //30
        ])

        when:
        table.allocate(experimentId, ["v1","base"], percentage, sharedBase)

        then:
        def e = thrown(IllegalArgumentException)
        println e.getMessage()

        where:
        experimentId  |  sharedBase  | percentage
        'exp1'        |  true        | 51
        'exp2'        |  false       | 21
        'exp2'        |  true        | 36
    }

    def "should prevent from allocating without base"(){
        given:
        def table = new AllocationTable([
                forSharedBase(50, 80),
                forVariant('exp1', 'v1', 0, 30),
        ])

        when:
        table.allocate("exp1", ["v1"], 30)

        then:
        def e = thrown(IllegalArgumentException)
        println e.getMessage()
    }

    def "should allocate even if records are fragmented"(){
        given:
        def table = new AllocationTable([
                forVariant("exp1", "v1",  0,   20), //20
                forVariant("exp1", "v1",  21,  40), //19
                forVariant("exp1", "v1",  41,  60), //19
                forVariant("exp1", "v1",  61,  80), //19
                forSharedBase(80, 100)
        ])

        when:
        assert table.sharedBaseAllocationSum == 20
        assert table.nonBaseAllocationSum == 77
        table = table.allocate("exp2", ["v1", "base"], 3, true)

        then:
        table.sharedBaseAllocationSum == 20
        table.nonBaseAllocationSum == 80
        table.getRecords().size() == 8
        table.getVariantAllocation("exp2","v1") == 3
        table.getVariantAllocation("exp1","v1") == 77
    }

    def "should allocate free space on empty table and allow adding experiments and scaling"(){
        given:
        def table = new AllocationTable([])

        when:
        table = table.allocate("exp1", ["v1","v2", "base"], 5)

        then:
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forSharedBase(95, 100)

        when:
        table = table.allocate("exp1", ["v1", "v2", "base"], 10)

        then:
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forVariant("exp1", "v1",  10, 15)
        table.records[3] == forVariant("exp1", "v2",  15, 20)
        table.records[4] == forSharedBase(90, 100)

        when:
        table = table.allocate("exp2", ["v1", "base"], 10)

        then:
        table.sharedBaseAllocationSum == 10
        table.nonBaseAllocationSum == 30
        table.records[0] == forVariant("exp1", "v1",  0,   5)
        table.records[1] == forVariant("exp1", "v2",  5,  10)
        table.records[2] == forVariant("exp1", "v1",  10, 15)
        table.records[3] == forVariant("exp1", "v2",  15, 20)
        table.records[4] == forVariant("exp2", "v1",  20, 30)
        table.records[5] == forSharedBase(90, 100)

    }
}