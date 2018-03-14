package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryInteractionRepository implements InteractionRepository {
    private final List<Interaction> interactions = new ArrayList<>();

    public void save(Interaction interaction) {
        interactions.add(interaction);
    }

    public boolean interactionSaved(Interaction interaction) {
        return interactions.contains(interaction);
    }
}
