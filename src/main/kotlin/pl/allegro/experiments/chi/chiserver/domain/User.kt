package pl.allegro.experiments.chi.chiserver.domain

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment

data class User(val name: String, val groups: List<String>, val isRoot: Boolean) {
    fun canEdit(experiment: Experiment): Boolean {
        return groups.any { g -> experiment.groups.contains(g) }
    }
}
