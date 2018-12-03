package pl.allegro.experiments.chi.chiserver.domain.scorer;

public class ToManyOffersException extends RuntimeException {
    public ToManyOffersException() {
        super("Maximum number of offers is 200");
    }
}
