package pl.allegro.tech.leaders.hackathon.registration;

import java.util.List;

public interface TeamRepository {
    void save(Team team);

    List<Team> findAll();

    Team get(String teamId);

    void deleteAll();
}
