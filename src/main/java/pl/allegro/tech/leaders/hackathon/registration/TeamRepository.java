package pl.allegro.tech.leaders.hackathon.registration;

import java.util.List;

interface TeamRepository {
    void save(Team team);

    List<Team> findAll();
}
