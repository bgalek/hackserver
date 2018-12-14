package pl.allegro.tech.leaders.hackathon.registration;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

class MongoTeamRepository implements TeamRepository {

    private final SpringDataTeamRepository delegate;

    MongoTeamRepository(SpringDataTeamRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(Team team) {
        delegate.save(team);
    }

    @Override
    public List<Team> findAll() {
        return StreamSupport.stream(delegate.findAll().spliterator(), false).collect(toList());
    }

}
