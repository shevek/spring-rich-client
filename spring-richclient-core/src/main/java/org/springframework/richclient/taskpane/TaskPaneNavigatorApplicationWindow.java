package org.springframework.richclient.taskpane;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.config.NavigatorApplicationLifecycleAdvisor;
import org.springframework.richclient.application.support.DefaultApplicationWindow;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;

import javax.swing.*;

public class TaskPaneNavigatorApplicationWindow extends DefaultApplicationWindow
{
    private JSplitPane framedPage;

    private boolean onlyOneExpanded;

    private IconGenerator<AbstractCommand> taskPaneIconGenerator;

    @Override
    protected JComponent createWindowContentPane()
    {
        CommandGroup navigationCommandGroup = ((NavigatorApplicationLifecycleAdvisor) getAdvisor())
                .getNavigationCommandGroup();
        TaskPaneNavigatorView taskPaneNavigatorView = new TaskPaneNavigatorView(navigationCommandGroup);
        taskPaneNavigatorView.setIconGenerator(getTaskPaneIconGenerator());
        taskPaneNavigatorView.setOnlyOneExpanded(onlyOneExpanded);

        framedPage = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(taskPaneNavigatorView.getControl(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
                null);
        framedPage.setOneTouchExpandable(true);

        return framedPage;
    }

    public boolean hasOnlyOneExpanded()
    {
        return onlyOneExpanded;
    }

    public void setOnlyOneExpanded(boolean onlyOneExpanded)
    {
        this.onlyOneExpanded = onlyOneExpanded;
    }

    @Override
    protected void setActivePage(ApplicationPage page)
    {
        framedPage.setRightComponent(page.getControl());
        framedPage.revalidate();
    }

    public IconGenerator<AbstractCommand> getTaskPaneIconGenerator()
    {
        return taskPaneIconGenerator;
    }

    public void setTaskPaneIconGenerator(IconGenerator<AbstractCommand> taskPaneIconGenerator)
    {
        this.taskPaneIconGenerator = taskPaneIconGenerator;
    }
}

