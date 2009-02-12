package org.springframework.richclient.taskpane;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ApplicationWindowFactory;
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.command.AbstractCommand;

public class TaskPaneNavigatorApplicationWindowFactory implements ApplicationWindowFactory
{
    private IconGenerator<AbstractCommand> taskPaneIconGenerator;

    public ApplicationWindow createApplicationWindow()
    {
        ApplicationLifecycleAdvisor lifecycleAdvisor = Application.instance().getLifecycleAdvisor();
        if (lifecycleAdvisor instanceof TaskPaneNavigatorApplicationLifecycleAdvisor)
        {
            TaskPaneNavigatorApplicationLifecycleAdvisor taskPaneNavigatorApplicationLifecycleAdvisor = (TaskPaneNavigatorApplicationLifecycleAdvisor) lifecycleAdvisor;
            TaskPaneNavigatorApplicationWindow window = new TaskPaneNavigatorApplicationWindow();
            window.setTaskPaneIconGenerator(getTaskPaneIconGenerator());
            window.setOnlyOneExpanded(taskPaneNavigatorApplicationLifecycleAdvisor.hasOnlyOneExpanded());
            return window;
        }
        else
        {
            throw new IllegalArgumentException();
        }
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
