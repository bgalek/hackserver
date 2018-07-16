package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.scheduling.annotation.Scheduled;

public class FileBasedExperimentsRepositoryRefresher {
    private static final long REFRESH_RATE_IN_SECONDS = 10;
    private final FileBasedExperimentsRepository experimentsRepository;

    public FileBasedExperimentsRepositoryRefresher(FileBasedExperimentsRepository experimentsRepository) {
        this.experimentsRepository = experimentsRepository;
    }

    @Scheduled(fixedDelay = REFRESH_RATE_IN_SECONDS * 1_000,
            initialDelay = REFRESH_RATE_IN_SECONDS * 1_000)
    public void refresh() {
        experimentsRepository.secureRefresh();
    }
}
