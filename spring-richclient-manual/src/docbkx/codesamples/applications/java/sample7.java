public class MultipleToolbarApplicationWindow extends DefaultApplicationWindow
{
    private CommandGroup[] toolBarCommandGroups;

    protected void init()
    {
        super.init();
        if(getAdvisor() instanceof CustomApplicationLifecycleAdvisor)
        {
            this.toolBarCommandGroups = ((CustomApplicationLifecycleAdvisor) getAdvisor()).getToolBarCommandGroups();
        }
        else
        {
            this.toolBarCommandGroups = new CommandGroup[] {getAdvisor().getToolBarCommandGroup()};
        }
    }

    protected JComponent createToolBarControl() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(toolBarCommandGroups.length, 1));
        for (int i = 0; i < toolBarCommandGroups.length; i++)
        {
            CommandGroup toolBarCommandGroup = toolBarCommandGroups[i];
            JComponent toolBar = toolBarCommandGroup.createToolBar();
        toolBarCommandGroup.setVisible( getWindowConfigurer().getShowToolBar() );
            panel.add(toolBar);

        }
        return panel;
    }
}