package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.CommandFactory;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicStatisticsForVariantMetricRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository;
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class ClassicStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(ClassicStatisticsController.class);

    private final CommandFactory commandFactory;
    private final Gson jsonConverter;
    private final ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository;
    private final StatisticsRepository statisticsRepository;
    public static final String CHI_TOKEN = "AD34C2FAB59636A423F8A2D7F7696";

    public ClassicStatisticsController(CommandFactory commandFactory,
                                       Gson jsonConverter,
                                       ClassicStatisticsForVariantMetricRepository classicStatisticsForVariantMetricRepository,
                                       StatisticsRepository statisticsRepository) {
        this.jsonConverter = jsonConverter;
        this.classicStatisticsForVariantMetricRepository = classicStatisticsForVariantMetricRepository;
        this.statisticsRepository = statisticsRepository;
        this.commandFactory = commandFactory;
    }

    @MeteredEndpoint
    @GetMapping("/admin/statistics/{experimentId}")
    ResponseEntity<String> experimentStatistics(@PathVariable String experimentId) {
        logger.debug("Experiment statistics request received");
        return ResponseEntity.ok(jsonConverter.toJson(statisticsRepository.getExperimentStatistics(experimentId)));
    }

    @MeteredEndpoint
    @PostMapping(value = "/statistics", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @PublicEndpoint
    ResponseEntity postStatistics(
            @RequestBody String stats,
            @RequestHeader(value = "Chi-Token", defaultValue = "") String chiToken) {

        if (!chiToken.equals(CHI_TOKEN)) {
            return ResponseEntity.badRequest().build();
        }

        var classicStats = jsonConverter.fromJson(stats, ClassicExperimentStatisticsForVariantMetric.class);

        logger.info("Classic stats received: {} {} {} {} {} {}",
                classicStats.getExperimentId(),
                classicStats.getDevice(),
                classicStats.getToDate(),
                classicStats.getVariantName(),
                classicStats.getMetricName());

        classicStatisticsForVariantMetricRepository.save(classicStats);

        commandFactory.updateBaselineMetricValueCommand(classicStats).execute();

        return ResponseEntity.ok().build();
    }
}
