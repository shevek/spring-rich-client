public class DefaultApplicationWindowFactory implements ApplicationWindowFactory
{
    private static final Log logger = LogFactory.getLog(DefaultApplicationWindowFactory.class);

    public ApplicationWindow createApplicationWindow()
    {
        ApplicationLifecycleAdvisor lifecycleAdvisor = Application.instance().getLifecycleAdvisor();
        if (lifecycleAdvisor instanceof OutlookNavigatorApplicationLifecycleAdvisor)
        {
             return OutlookNavigatorApplicationWindowFactory.create();
        }
        else if (lifecycleAdvisor instanceof TaskPaneNavigatorApplicationLifecycleAdvisor)
        {
             return TaskPaneNavigatorApplicationWindowFactory.create();
        }
        return new DefaultApplicationWindow();
    }

    static class TaskPaneNavigatorApplicationWindowFactory
    {
        public static ApplicationWindow create(boolean onlyOneExpanded)
        {
            ...
        }
    }

    static class OutlookNavigatorApplicationWindowFactory
    {
        public static ApplicationWindow create()
        {
            ...
        }
    }
}