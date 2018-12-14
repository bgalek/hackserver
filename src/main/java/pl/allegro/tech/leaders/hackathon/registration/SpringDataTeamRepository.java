package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data
 */
interface SpringDataTeamRepository extends CrudRepository<Team, String> {
}
