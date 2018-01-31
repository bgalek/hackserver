package pl.allegro.experiments.chi.chiserver.domain

data class User(val name: String, val groups: List<String>, val isRoot: Boolean)
