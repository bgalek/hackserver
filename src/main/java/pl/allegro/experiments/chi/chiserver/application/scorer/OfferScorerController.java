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

import java.util.Collections;
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
    private final OfferParametersRepository offerParametersRepository;
    private final OfferRepository offerRepository;
    public static final String CHI_TOKEN = "DSFFT346SF4643332HSSSGHBAUWRTUYAETGNVCH";

    public OfferScorerController(
            OfferRepository offerRepository,
            OfferParametersRepository offerParametersRepository) {
        this.offerParametersRepository = offerParametersRepository;
        this.offerRepository = offerRepository;
    }

    @MeteredEndpoint
    @PostMapping(path = {"/offers"})
    void setOffers(@RequestBody List<Offer> offers) {
        offerRepository.setOffers(new HashSet<>(offers));
    }

    @MeteredEndpoint
    @GetMapping(path = {"/parameters"})
    List<OfferParameters> parameters() {
        return offerParametersRepository.all();
    }

    @Deprecated
    @MeteredEndpoint
    @GetMapping(path = {"/scores"})
    List<OfferParameters> scores() {
        // grouper still uses client that require /scores endpoint
        return Collections.emptyList();
    }

    @MeteredEndpoint
    @PostMapping(value = "/parameters", consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @PublicEndpoint
    void setOfferParameters(
            @RequestBody List<OfferParameters> offerParameters,
            @RequestHeader(value = "Chi-Token", defaultValue = "") String chiToken) {
        if (!chiToken.equals(CHI_TOKEN)) {
            throw new UnauthorizedPublicApiCallException();
        }
        offerParametersRepository.update(offerParameters);
    }

    @ExceptionHandler(ToManyOffersException.class)
    ResponseEntity<ErrorsHolder> handleToManyOffers(ToManyOffersException exception) {
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
