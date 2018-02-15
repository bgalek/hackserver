package pl.allegro.experiments.chi.chiserver.domain.experiments

import pl.allegro.experiments.chi.chiserver.domain.UserProvider

class PermissionsRepository(private val userProvider: UserProvider,
                            private val experimentsRepository : ExperimentsRepository) {
    fun withPermissions(experiment: Experiment): Experiment {
        val currentUser = userProvider.getCurrentUser()
        return experiment.withEditableFlag(currentUser.isOwner(experiment))
                         .withOrigin(experimentsRepository.getOrigin(experiment.id))
    }
}