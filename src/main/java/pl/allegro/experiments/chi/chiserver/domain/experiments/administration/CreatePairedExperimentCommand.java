package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class CreatePairedExperimentCommand implements Command {

    private final CreateExperimentCommand createExperimentCommand;
    private final CreateExperimentGroupCommand createExperimentGroupCommand;
    private final DeleteExperimentCommand deleteExperimentCommand;


    public CreatePairedExperimentCommand(
            CreateExperimentCommand createExperimentCommand,
            CreateExperimentGroupCommand createExperimentGroupCommand,
            DeleteExperimentCommand deleteExperimentCommand) {
        this.createExperimentCommand = createExperimentCommand;
        this.createExperimentGroupCommand = createExperimentGroupCommand;
        this.deleteExperimentCommand = deleteExperimentCommand;
    }

    public void execute() {
        createExperimentCommand.execute();
        try {
            createExperimentGroupCommand.execute();
        } catch (ExperimentCommandException e) {
            deleteExperimentCommand.execute();
            throw e;
        }
    }
}
