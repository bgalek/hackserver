package pl.allegro.experiments.chi.chiserver.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import pl.allegro.experiments.chi.chiserver.BaseE2EIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.AllocationTable
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory
import spock.lang.Unroll

import java.time.ZonedDateTime

import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus.*
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertHasSpecifiedInternalVariantWithDeviceClass
import static pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch.ClientExperimentAssertions.assertShredRangeWithDeviceClass

class ExperimentGroupE2ESpec extends BaseE2EIntegrationSpec {
    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    ClientExperimentFactory clientExperimentFactory

    def prepareFod() {
        def fod_generaldelivery_show_delivery_points = experimentDefinition().id("fod_generaldelivery_show_delivery_points")
                .variantNames([
                    "base",
                    "showallpoints"
                ])
                .percentage(1)
                .activityPeriod(ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1))
                .build()

        def delivery_groups_experiment_09_2018_v1 = experimentDefinition().id("delivery-groups-experiment-09_2018_v1")
                .variantNames([
                    "base",
                    "base1",
                    "radio",
                    "tile"
                ])
                .percentage(5)
                .activityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(10))
                .build()

        def fod_generaldelivery_show_delivery_points_v2 = experimentDefinition().id("fod_generaldelivery_show_delivery_points_v2")
                .variantNames([
                    "base",
                    "showallpoints"
                ])
                .percentage(30)
                .activityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(10))
                .build()

        experimentsRepository.save(fod_generaldelivery_show_delivery_points)
        experimentsRepository.save(delivery_groups_experiment_09_2018_v1)
        experimentsRepository.save(fod_generaldelivery_show_delivery_points_v2)

        def group = new ExperimentGroup(
                UUID.randomUUID().toString(),
                "fod",
                [
                        fod_generaldelivery_show_delivery_points.id,
                        delivery_groups_experiment_09_2018_v1.id,
                        fod_generaldelivery_show_delivery_points_v2.id
                ],
                AllocationTable.empty())

        experimentGroupRepository.save(group)
        group
    }

    def prepareListingi() {
        def listing_interline = experimentDefinition().id("listing_interline")
                .variantNames([
                    "base",
                    "rating-popover-cheaper-index5",
                    "rating-list-cheaper-index10",
                    "rating-popover-no-coins-shipping-index5",
                    "rating-list-no-coins-shipping-index10",
                    "no-coins-suggested-filters-index5",
                    "suggested-filters-index10",
                    "suggested-links-index5",
                    "suggested-links-index10"
                ])
                .percentage(5)
                .activityPeriod(ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1))
                .build()

        def listing_average_product_rating = experimentDefinition().id("listing_average_product_rating")
            .variantNames([
                    "base",
                    "rating-popover-cheaper-index5",
                    "rating-list-cheaper-index10",
                    "rating-popover-no-coins-shipping-index5",
                    "rating-list-no-coins-shipping-index10",
                    "no-coins-suggested-filters-index5"
            ])
            .percentage(5)
            .activityPeriod(ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1))
            .build()

        def mweb_spa_listing_extended = experimentDefinition().id("mweb_spa_listing_extended")
                .variantNames([
                    "base",
                    "enabled",
                    "simplified"
                ])
                .percentage(10)
                .activityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(10))
                .deviceClass(DeviceClass.phone_android)
                .internalVariantName("base")
                .build()

        experimentsRepository.save(listing_interline)
        experimentsRepository.save(listing_average_product_rating)
        experimentsRepository.save(mweb_spa_listing_extended)

        def group = new ExperimentGroup(
                UUID.randomUUID().toString(),
                "mweb-spa-listing-final",
                [listing_interline.id, listing_average_product_rating.id, mweb_spa_listing_extended.id],
                AllocationTable.empty())

        experimentGroupRepository.save(group)
        group
    }

    @Unroll
    def "should delete #status experiment bounded to a group and free allocated space"() {
        given:
        def experiment1 = experimentWithStatus(status)
        def experiment2 = draftExperiment()
        def group = createExperimentGroupAndFetch([experiment1.id, experiment2.id])

        assert fetchExperimentGroup(group.id).allocationTable.size() == 4

        when:
        deleteExperiment(experiment1.id as String)

        then:
        def fetchedGroup = fetchExperimentGroup(group.id)
        fetchedGroup.experiments == [experiment2.id]
        fetchedGroup.allocationTable.size() == 2
        fetchedGroup.allocationTable[0].experimentId == experiment2.id
        fetchedGroup.allocationTable[1].experimentId == experiment2.id

        when:
        fetchExperiment(experiment1.id as String)

        then:
        def exception = thrown HttpClientErrorException
        exception.statusCode == HttpStatus.NOT_FOUND

        where:
        status << [ACTIVE, DRAFT, PAUSED]
    }

    def "should delete ENDED experiment bounded to a group and free allocated space "() {
        given:
        def exp = experimentWithStatus(ENDED)
        def group = new ExperimentGroup(UUID.randomUUID().toString(), "salt", [exp.id],
                new AllocationTable([]).allocate (exp.id, ['v1', 'base'], 10))

        experimentGroupRepository.save(group)
        assert fetchExperimentGroup(group.id).allocationTable.size() == 2

        when:
        deleteExperiment(exp.id as String)

        then:
        assert fetchExperimentGroup(group.id).allocationTable.size() == 0
    }
}
