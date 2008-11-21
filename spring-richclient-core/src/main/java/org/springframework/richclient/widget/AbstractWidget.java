package org.springframework.richclient.widget;

import org.springframework.richclient.application.support.ApplicationServicesAccessor;
import org.springframework.richclient.command.AbstractCommand;

import java.util.List;
import java.util.Collections;

/**
 * Default behavior implementation of AbstractWidget
 */
public abstract class AbstractWidget extends ApplicationServicesAccessor implements Widget
{
    protected boolean showing = false;

    /**
     * {@inheritDoc}
     */
    public void onAboutToShow()
    {
        showing = true;
    }

    /**
     * {@inheritDoc}
     */
    public void onAboutToHide()
    {
        showing = false;
    }

    public boolean isShowing()
    {
        return showing;
    }

    /**
     * {@inheritDoc}
     *
     * Default: Widget can be closed.
     */
    public boolean canClose()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AbstractCommand> getCommands()
    {
        return Collections.emptyList();
    }
}