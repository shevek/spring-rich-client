public class CustomApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
    private List toolBarBeanNames;

    public void setToolBarBeanNames(List toolBarBeanNames)
    {
        this.toolBarBeanNames = toolBarBeanNames;
    }

    public void setToolbarBeanName(String toolbarBeanName)
    {
        toolBarBeanNames = new ArrayList();
        toolBarBeanNames.add(toolbarBeanName);
    }

    public CommandGroup[] getToolBarCommandGroups()
    {
        if(toolBarBeanNames == null || toolBarBeanNames.size() == 0)
        {
            return new CommandGroup[] { new CommandGroup() };
        }
        else
        {
            CommandGroup[] groups = new CommandGroup[toolBarBeanNames.size()];
            for (int i = 0; i < toolBarBeanNames.size(); i++)
            {
                groups[i] = getCommandGroup(toolBarBeanNames.get(i).toString());
            }
            return groups;
        }
    }
}