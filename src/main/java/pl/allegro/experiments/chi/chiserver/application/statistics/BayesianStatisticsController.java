package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.*;
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.allegro.experiments.chi.chiserver.application.statistics.ClassicStatisticsController.CHI_TOKEN;

@RestController
@RequestMapping(value = {"/api/bayes"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class BayesianStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsController.class);

    private final Gson jsonConverter;
    private final BayesianStatisticsForVariantRepository bayesianStatisticsRepository;
    private final BayesianChartsRepository bayesianChartsRepository;

    public BayesianStatisticsController(Gson jsonConverter,
                                        BayesianStatisticsForVariantRepository bayesianStatisticsRepository,
                                        BayesianChartsRepository bayesianChartsRepository) {
        this.jsonConverter = jsonConverter;
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
        this.bayesianChartsRepository = bayesianChartsRepository;
    }

    @MeteredEndpoint
    @GetMapping("/verticalEqualizer/{experimentId}")
    ResponseEntity<String> verticalEqualizer(
            @PathVariable String experimentId) {
        return ResponseEntity.ok(jsonConverter.toJson(bayesianChartsRepository.getVerticalEqualizer(experimentId)));
    }

    @MeteredEndpoint
    @GetMapping("/histograms/{experimentId}")
    ResponseEntity<String> histogram(
            @PathVariable String experimentId) {
        return ResponseEntity.ok(jsonConverter.toJson(bayesianChartsRepository.getHistograms(experimentId)));
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

        var bayesianStats = jsonConverter.fromJson(stats, BayesianExperimentStatisticsForVariant.class);

        logger.info("Bayesian stats received: {} {} {} {}",
                bayesianStats.getExperimentId(),
                bayesianStats.getDevice(),
                bayesianStats.getToDate(),
                bayesianStats.getVariantName());

        bayesianStatisticsRepository.save(bayesianStats);

        return ResponseEntity.ok().build();
    }
}
