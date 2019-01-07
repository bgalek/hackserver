package pl.allegro.experiments.chi.chiserver.domain.scorer;

import java.util.List;

public interface OfferParametersRepository {

    List<OfferParameters> all();

    void update(List<OfferParameters> offersParameters);
}
