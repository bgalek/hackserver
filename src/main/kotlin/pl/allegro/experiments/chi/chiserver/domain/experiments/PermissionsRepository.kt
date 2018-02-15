package pl.allegro.experiments.chi.chiserver.domain.experiments

import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository

class PermissionsRepository(private val userProvider: UserProvider,
                            private val experimentsRepository : ExperimentsDoubleRepository) {
    fun withPermissions(experiment: Experiment): Experiment {
        val currentUser = userProvider.getCurrentUser()
        return experiment.withEditableFlag(currentUser.isOwner(experiment))
                         .withOrigin(experimentsRepository.getOrigin(experiment.id))
    }
}