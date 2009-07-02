public class MessageBoxCommand extends ActionCommand
{
    protected void doExecuteCommand()
    {
        JOptionPane.showMessageDialog(Application.instance().getActiveWindow().getControl(), "Hello world!");
    }
}