package pl.allegro.experiments.chi.chiserver.domain.experiments

import pl.allegro.experiments.chi.chiserver.domain.UserProvider

class PermissionsRepository(private val userProvider: UserProvider) {
    fun withPermissions(experiment: Experiment): Experiment {
        val currentUser = userProvider.getCurrentUser()
        return if (currentUser.isOwner(experiment)) experiment.markAsEditable() else experiment
    }
}