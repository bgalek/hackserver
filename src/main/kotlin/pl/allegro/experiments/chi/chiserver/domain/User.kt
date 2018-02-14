package pl.allegro.experiments.chi.chiserver.domain

import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment

data class User(
        val name: String,
        val groups: List<String>,
        val isRoot: Boolean) {

    companion object {
        const val ANONYMOUS = "Anonymous"
    }

    fun isOwner(experiment: Experiment): Boolean {
        return isRoot or groups.any { g -> experiment.groups.contains(g) } or isAuthor(experiment)
    }

    fun isAnonymous(): Boolean {
        return name == ANONYMOUS
    }

    private fun isAuthor(experiment: Experiment): Boolean {
        return if (experiment.author != null) name == experiment.author else false
    }
}