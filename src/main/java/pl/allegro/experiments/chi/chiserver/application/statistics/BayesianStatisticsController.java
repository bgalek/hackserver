package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api/bayes"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class BayesianStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(BayesianStatisticsController.class);

    private final Gson jsonConverter;
    private final BayesianStatisticsRepository bayesianStatisticsRepository;
    private final StatisticsRepository statisticsRepository;

    public BayesianStatisticsController(Gson jsonConverter,
                                        BayesianStatisticsRepository bayesianStatisticsRepository,
                                        StatisticsRepository statisticsRepository) {
        this.jsonConverter = jsonConverter;
        this.bayesianStatisticsRepository = bayesianStatisticsRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @MeteredEndpoint
    @GetMapping("/statistics/{experimentId}")
    ResponseEntity<String> experimentsStatistics(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) String device,
            @RequestParam(value = "toDate", required = false) String toDate) {
        logger.info("Experiment bayesian statistics request received {} device {} toDate {}", experimentId, device, toDate);

        if (device == null) {
            device = "all";
        }

        if (toDate == null) {
            toDate = Optional.ofNullable(statisticsRepository.lastStatisticsDate(experimentId))
                    .map(LocalDate::toString)
                    .orElse(null);
            if (toDate == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        return bayesianStatisticsRepository.experimentStatistics(experimentId, device, toDate)
                .map(s -> ResponseEntity.ok(jsonConverter.toJson(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @MeteredEndpoint
    @PostMapping(value = "/statistics", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    ResponseEntity postStatistics(@RequestBody BayesianExperimentStatistics stats) {
       bayesianStatisticsRepository.save(stats);
       return ResponseEntity.ok().build();
    }
}
