package pl.allegro.experiments.chi.chiserver.domain.scorer;

public class OfferScoreValueOutOfBoundsException extends RuntimeException {
    public OfferScoreValueOutOfBoundsException() {
        super("Offer score value of of <0, 1>");
    }
}
