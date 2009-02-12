package org.springframework.richclient.taskpane;

import org.springframework.richclient.application.config.NavigatorApplicationLifecycleAdvisor;

public class TaskPaneNavigatorApplicationLifecycleAdvisor extends NavigatorApplicationLifecycleAdvisor
{
    private boolean onlyOneExpanded = true;

    public boolean hasOnlyOneExpanded()
    {
        return onlyOneExpanded;
    }

    public void setOnlyOneExpanded(boolean onlyOneExpanded)
    {
        this.onlyOneExpanded = onlyOneExpanded;
    }
}