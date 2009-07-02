public class LoginLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
    private LoginHandler handler;

    public void setHandler(final LoginHandler handler)
    {
        this.handler = handler;
    }

    public void onCommandsCreated(final ApplicationWindow window)
    {
        super.onCommandsCreated(window);
        handler.doLogin();
    }
}