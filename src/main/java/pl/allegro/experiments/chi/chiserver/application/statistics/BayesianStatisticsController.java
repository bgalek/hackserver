package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianEqualizersRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api/bayes"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class BayesianStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsController.class);

    private final Gson jsonConverter;
    private final BayesianStatisticsRepository bayesianStatisticsRepository;
    private final BayesianEqualizersRepository bayesianEqualizersRepository;
    private final StatisticsRepository statisticsRepository;

    public BayesianStatisticsController(Gson jsonConverter,
                                        BayesianStatisticsRepository bayesianStatisticsRepository,
                                        StatisticsRepository statisticsRepository,
                                        BayesianEqualizersRepository bayesianEqualizersRepository) {
        this.jsonConverter = jsonConverter;
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
        this.bayesianEqualizersRepository = bayesianEqualizersRepository;
    }

    @MeteredEndpoint
    @GetMapping("/verticalEqualizer/{experimentId}")
    ResponseEntity<String> verticalEqualizer(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) final String device) {

        DeviceClass usedDeviceClass = DeviceClass.fromString(device);
        LocalDate lastStatisticsDate = statisticsRepository.lastStatisticsDate(experimentId);
        logger.info("request for bayesian verticalEqualizer received, experimentId: {}, device: {}, toDate: {} ", experimentId, usedDeviceClass, lastStatisticsDate);

        if (lastStatisticsDate == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return bayesianEqualizersRepository.getVerticalEqualizer(experimentId, usedDeviceClass, lastStatisticsDate)
                .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @MeteredEndpoint
    @GetMapping("/statistics/{experimentId}")
    ResponseEntity<String> experimentsStatistics(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false, defaultValue = "all") final String device) {
        LocalDate lastStatisticsDate = statisticsRepository.lastStatisticsDate(experimentId);
        logger.info("request for bayesian histogram received, experimentId: {}, device: {}, toDate: {} ", experimentId, device, lastStatisticsDate);

        if (lastStatisticsDate == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return bayesianStatisticsRepository.experimentStatistics(experimentId, device, lastStatisticsDate.toString())
                        .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                        .orElse(ResponseEntity.notFound().build());
    }

    @MeteredEndpoint
    @PostMapping(value = "/statistics", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity postStatistics(@RequestBody BayesianExperimentStatistics stats) {
        String variants = String.join(",", stats.getVariantBayesianStatistics().stream().map(VariantBayesianStatistics::getVariantName).collect(Collectors.toList()));
        logger.info("Bayesian stats received: {} device {} toDate {} variants {}", stats.getExperimentId(), stats.getDevice(), stats.getToDate(), variants);
        bayesianStatisticsRepository.save(stats);
        return ResponseEntity.ok().build();
    }
}
