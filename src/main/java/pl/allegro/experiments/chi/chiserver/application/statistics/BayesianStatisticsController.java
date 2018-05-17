package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianChartsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api/bayes"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class BayesianStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsController.class);

    private final Gson jsonConverter;
    private final BayesianStatisticsRepository bayesianStatisticsRepository;
    private final BayesianChartsRepository bayesianChartsRepository;

    public BayesianStatisticsController(Gson jsonConverter,
                                        BayesianStatisticsRepository bayesianStatisticsRepository,
                                        BayesianChartsRepository bayesianChartsRepository) {
        this.jsonConverter = jsonConverter;
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
        this.bayesianChartsRepository = bayesianChartsRepository;
    }

    @MeteredEndpoint
    @GetMapping("/verticalEqualizer/{experimentId}")
    ResponseEntity<String> verticalEqualizer(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) final String device) {
        return bayesianChartsRepository.getVerticalEqualizer(experimentId, DeviceClass.fromString(device))
                        .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                        .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @MeteredEndpoint
    @GetMapping("/histograms/{experimentId}")
    ResponseEntity<String> histogram(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) final String device) {
        return bayesianChartsRepository.getHistograms(experimentId, DeviceClass.fromString(device))
                .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @MeteredEndpoint
    @PostMapping(value = "/statistics", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity postStatistics(@RequestBody String stats) {
        BayesianExperimentStatistics bayesianExperimentStatistics = jsonConverter.fromJson(stats, BayesianExperimentStatistics.class);

        String variants = String.join(",", bayesianExperimentStatistics.getVariantBayesianStatistics().stream().map(VariantBayesianStatistics::getVariantName).collect(Collectors.toList()));
        logger.info("Bayesian stats received: {} device {} toDate {} variants {}", bayesianExperimentStatistics.getExperimentId(), bayesianExperimentStatistics.getDevice(), bayesianExperimentStatistics.getToDate(), variants);
        bayesianStatisticsRepository.save(bayesianExperimentStatistics);
        return ResponseEntity.ok().build();
    }
}
