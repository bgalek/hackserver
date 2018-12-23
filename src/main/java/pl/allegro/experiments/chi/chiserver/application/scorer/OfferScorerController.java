package pl.allegro.experiments.chi.chiserver.application.scorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.allegro.experiments.chi.chiserver.domain.scorer.*;
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint;
import pl.allegro.tech.common.andamio.errors.Error;
import pl.allegro.tech.common.andamio.errors.ErrorsHolder;
import pl.allegro.tech.common.andamio.errors.SimpleErrorsHolder;
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint;

import java.util.HashSet;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(
        value = {"/api/scorer"},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE}
)
public class OfferScorerController {

    private static final Logger logger = LoggerFactory.getLogger(OfferScorerController.class);
    private final OfferScoreRepository scoreRepository;
    private final OfferRepository offerRepository;
    public static final String CHI_TOKEN = "DSFFT346SF4643332HSSSGHBAUWRTUYAETGNVCH";

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
    @PostMapping(value = "/scores", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @PublicEndpoint
    void setOfferScores(
            @RequestBody List<OfferScore> offerScores,
            @RequestHeader(value = "Chi-Token", defaultValue = "") String chiToken) {
        if (!chiToken.equals(CHI_TOKEN)) {
            throw new UnauthorizedPublicApiCallException();
        }
        scoreRepository.updateScores(offerScores);
    }

    @ExceptionHandler(ToManyOffersException.class)
    ResponseEntity<ErrorsHolder> handleToManyOffers(ToManyOffersException exception) {
        return handleException(exception, "400");
    }

    @ExceptionHandler(OfferScoreValueOutOfBoundsException.class)
    ResponseEntity<ErrorsHolder> handleOfferScoreValueOutOfBounds(OfferScoreValueOutOfBoundsException exception) {
        return handleException(exception, "400");
    }

    @ExceptionHandler(UnauthorizedPublicApiCallException.class)
    ResponseEntity<ErrorsHolder> handleUnauthorizedPublicApiCall(UnauthorizedPublicApiCallException exception) {
        return handleException(exception, "403");
    }

    private ResponseEntity<ErrorsHolder> handleException(Exception exception, String statusCode) {
        logger.error(exception.getMessage());
        Error error = Error.error()
                .fromException(exception)
                .withCode(statusCode)
                .withMessage(exception.getMessage())
                .build();

        return new ResponseEntity<>(new SimpleErrorsHolder(error), HttpStatus.BAD_REQUEST);
    }
}
