package pl.allegro.experiments.chi.chiserver.application.scorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.scorer.*;
import pl.allegro.tech.common.andamio.errors.Error;
import pl.allegro.tech.common.andamio.errors.ErrorsHolder;
import pl.allegro.tech.common.andamio.errors.SimpleErrorsHolder;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping(
        value = {"/api/scorer"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class OfferScorerController {

    private static final Logger logger = LoggerFactory.getLogger(OfferScorerController.class);
    private final OfferScoreRepository scoreRepository;
    private final OfferRepository offerRepository;

    public OfferScorerController(
            OfferRepository offerRepository,
            OfferScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
        this.offerRepository = offerRepository;
    }

    @MeteredEndpoint
    @PostMapping(path = {"/offers"})
    void setOffers(@RequestBody List<Offer> offers) {
        offerRepository.setOffers(new HashSet<>(offers));
    }

    @MeteredEndpoint
    @GetMapping(path = {"/scores"})
    List<OfferScore> scores() {
        return scoreRepository.scores();
    }

    @MeteredEndpoint
    @PostMapping(path = {"/scores"})
    void setOfferScores(@RequestBody List<OfferScore> offerScores) {
        scoreRepository.setScores(offerScores);
    }

    @ExceptionHandler(ToManyOffersException.class)
    ResponseEntity<ErrorsHolder> handle(ToManyOffersException exception) {
        logger.error(exception.getMessage());
        Error error = Error.error()
                .fromException(exception)
                .withCode("400")
                .withMessage(exception.getMessage())
                .build();

        return new ResponseEntity<>(new SimpleErrorsHolder(error), HttpStatus.BAD_REQUEST);
    }
}
