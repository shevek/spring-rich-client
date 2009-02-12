package org.springframework.richclient.application.config;

import org.springframework.richclient.command.CommandGroup;

public abstract class NavigatorApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
    private String navigationBeanName;

    public void setNavigationBeanName(String navigationBeanName)
    {
        this.navigationBeanName = navigationBeanName;
    }

    public CommandGroup getNavigationCommandGroup()
    {
        return getCommandGroup(navigationBeanName);
    }
}
