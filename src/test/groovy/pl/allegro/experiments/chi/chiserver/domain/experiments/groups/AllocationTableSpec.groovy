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
      2                | 10          | true
      2                | 13          | true
      2                | 14          | false
      2                | 15          | false
      1                | 20          | true
      1                | 21          | false
    }
}
