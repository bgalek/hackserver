package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianChartsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.time.LocalDate;
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
    private final StatisticsRepository statisticsRepository;

    public BayesianStatisticsController(Gson jsonConverter,
                                        BayesianStatisticsRepository bayesianStatisticsRepository,
                                        StatisticsRepository statisticsRepository,
                                        BayesianChartsRepository bayesianChartsRepository) {
        this.jsonConverter = jsonConverter;
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
        this.bayesianChartsRepository = bayesianChartsRepository;
    }

    @MeteredEndpoint
    @GetMapping("/verticalEqualizer/{experimentId}")
    ResponseEntity<String> verticalEqualizer(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) final String device) {

        DeviceClass usedDeviceClass = DeviceClass.fromString(device);
        LocalDate lastStatisticsDate = statisticsRepository.lastStatisticsDate(experimentId);
        logger.info("request for bayesian VerticalEqualizer received, experimentId: {}, device: {}, toDate: {} ", experimentId, usedDeviceClass, lastStatisticsDate);

        if (lastStatisticsDate == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return bayesianChartsRepository.getVerticalEqualizer(experimentId, usedDeviceClass, lastStatisticsDate)
                .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @MeteredEndpoint
    @GetMapping("/histograms/{experimentId}")
    ResponseEntity<String> experimentsStatistics(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) final String device) {

        DeviceClass usedDeviceClass = DeviceClass.fromString(device);
        LocalDate lastStatisticsDate = statisticsRepository.lastStatisticsDate(experimentId);
        logger.info("request for bayesian Histograms received, experimentId: {}, device: {}, toDate: {} ", experimentId, usedDeviceClass, lastStatisticsDate);

        if (lastStatisticsDate == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return bayesianChartsRepository.getHistograms(experimentId, usedDeviceClass, lastStatisticsDate)
                .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                .orElse(ResponseEntity.notFound().build());
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
