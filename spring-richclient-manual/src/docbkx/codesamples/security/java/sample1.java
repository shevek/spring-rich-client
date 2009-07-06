public class PetClinicLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
    // omitted for brevity

    public void onCommandsCreated(ApplicationWindow window)
    {
        ActionCommand command = (ActionCommand) window.getCommandManager().getCommand("loginCommand",
                                 ActionCommand.class);
        command.execute();
    }
}