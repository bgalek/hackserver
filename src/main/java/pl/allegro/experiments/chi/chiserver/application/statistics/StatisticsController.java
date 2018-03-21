package pl.allegro.experiments.chi.chiserver.application.statistics;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = {"/api"}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
public class StatisticsController {
    private final ExperimentsRepository experimentsRepository;
    private final StatisticsRepository statisticsRepository;
    private final Gson jsonConverter;

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    StatisticsController(
            ExperimentsRepository experimentsRepository,
            StatisticsRepository statisticsRepository,
            Gson jsonConverter) {
        this.experimentsRepository = experimentsRepository;
        this.statisticsRepository = statisticsRepository;
        this.jsonConverter = jsonConverter;
    }

    @MeteredEndpoint
    @GetMapping("/statistics/{experimentId}")
    ResponseEntity<String> experimentsStatistics(
            @PathVariable String experimentId,
            @RequestParam(value = "device", required = false) String device) {
        logger.debug("Experiment statistics request received");
        Experiment experiment = experimentsRepository.getExperiment(experimentId).orElse(null);
        if (experiment == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        LocalDate lastDate = statisticsRepository.lastStatisticsDate(experiment);
        if (lastDate == null) {
            return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        }

        if (device == null) {
            device = "all";
        }

        return ResponseEntity.ok(jsonConverter.toJson(
                statisticsRepository.experimentStatistics(experiment, lastDate, device)
        ));
    }
}
