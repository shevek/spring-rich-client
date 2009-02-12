package org.springframework.richclient.taskpane;

import org.springframework.richclient.application.support.AbstractNavigatorView;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandGroupJComponentBuilder;

public class TaskPaneNavigatorView extends AbstractNavigatorView
{
    private boolean onlyOneExpanded = true;

    private IconGenerator<AbstractCommand> iconGenerator;

    public TaskPaneNavigatorView(CommandGroup navigation)
    {
        super(navigation);
    }

    public boolean hasOnlyOneExpanded()
    {
        return onlyOneExpanded;
    }

    public void setOnlyOneExpanded(boolean onlyOneExpanded)
    {
        this.onlyOneExpanded = onlyOneExpanded;
    }

    public CommandGroupJComponentBuilder getNavigationBuilder()
    {
        JTaskPaneBuilder navigationBuilder = new JTaskPaneBuilder();
        navigationBuilder.setIconGenerator(getIconGenerator());
        navigationBuilder.setOnlyOneExpanded(onlyOneExpanded);
        return navigationBuilder;
    }

    public IconGenerator<AbstractCommand> getIconGenerator()
    {
        return iconGenerator;
    }

    public void setIconGenerator(IconGenerator<AbstractCommand> iconGenerator)
    {
        this.iconGenerator = iconGenerator;
    }
}
