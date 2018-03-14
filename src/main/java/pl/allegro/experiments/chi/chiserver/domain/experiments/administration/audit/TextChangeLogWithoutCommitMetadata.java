package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.audit;

import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.commit.CommitMetadata;

class TextChangeLogWithoutCommitMetadata extends SimpleTextChangeLog {

    @Override
    public void onCommit(CommitMetadata commitMetadata) {

    }
}
