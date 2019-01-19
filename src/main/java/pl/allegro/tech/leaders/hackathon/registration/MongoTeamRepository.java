package pl.allegro.tech.leaders.hackathon.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

class MongoTeamRepository implements TeamRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoTeamRepository.class);
    private final SpringDataTeamRepository delegate;

    MongoTeamRepository(SpringDataTeamRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(Team team) {
        delegate.save(team);
        logger.info(team + " saved");
    }

    @Override
    public List<Team> findAll() {
        return StreamSupport.stream(delegate.findAll().spliterator(), false).collect(toList());
    }

    public Team get(String teamId) {
        return delegate.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
    }

    @Override
    public void deleteAll() {
        delegate.deleteAll();
    }
}
