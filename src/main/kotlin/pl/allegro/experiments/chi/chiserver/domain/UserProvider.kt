package pl.allegro.experiments.chi.chiserver.domain

interface UserProvider {
    fun getCurrentUser(): User
}