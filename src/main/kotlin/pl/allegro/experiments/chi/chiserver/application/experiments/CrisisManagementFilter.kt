package pl.allegro.experiments.chi.chiserver.application.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment

interface CrisisManagementFilter {
    fun filter(experiment: Experiment) : Boolean
}

class WhitelistCrisisManagementFilter(val whitelist: List<String>) : CrisisManagementFilter {
    override fun filter(experiment: Experiment) : Boolean {
        return whitelist.contains(experiment.id)
    }
}

class AllEnabledCrisisManagementFilter() : CrisisManagementFilter {
    override fun filter(experiment: Experiment) : Boolean {
        return true
    }
}
