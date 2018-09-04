package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class CreatePairedExperimentCommand implements Command {

    private final CreateExperimentCommand createExperimentCommand;
    private final AddExperimentToGroupCommand addExperimentToGroupCommand;
    private final DeleteExperimentCommand deleteExperimentCommand;


    public CreatePairedExperimentCommand(
            CreateExperimentCommand createExperimentCommand,
            AddExperimentToGroupCommand addExperimentToGroupCommand,
            DeleteExperimentCommand deleteExperimentCommand) {
        this.createExperimentCommand = createExperimentCommand;
        this.addExperimentToGroupCommand = addExperimentToGroupCommand;
        this.deleteExperimentCommand = deleteExperimentCommand;
    }

    public void execute() {
        createExperimentCommand.execute();
        try {
            addExperimentToGroupCommand.execute();
        } catch (ExperimentCommandException e) {
            deleteExperimentCommand.execute();
            throw e;
        }
    }
}
