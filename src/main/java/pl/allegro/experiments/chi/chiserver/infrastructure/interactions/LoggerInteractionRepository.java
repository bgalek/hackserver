package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;
import pl.allegro.experiments.chi.chiserver.domain.interactions.InteractionRepository;

import java.util.logging.Logger;

public class LoggerInteractionRepository implements InteractionRepository {
    public static final Logger logger = Logger.getLogger(LoggerInteractionRepository.class.getName());

    @Override
    public void save(Interaction interaction) {
        logger.info(
                "userId: " + interaction.getUserId() + "\n" +
                "userCmId: " + interaction.getUserCmId() + "\n" +
                "experimentId: " + interaction.getExperimentId() + "\n" +
                "variantName: " + interaction.getVariantName() + "\n" +
                "internal: " + interaction.getInternal() + "\n" +
                "deviceClass: " + interaction.getDeviceClass() + "\n" +
                "interactionDate: " + interaction.getInteractionDate()
        );
    }
}
