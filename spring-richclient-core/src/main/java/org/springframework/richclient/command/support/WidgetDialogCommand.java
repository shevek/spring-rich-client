package org.springframework.richclient.command.support;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.TitledWidgetApplicationDialog;

import java.awt.*;

/**
 * Widget Dialog Command shows a specific widget in a dialog.
 */
public class WidgetDialogCommand extends AbstractWidgetCommand
{
    private ApplicationDialog dialog;

    /** parent for centering the dialog. */
    private Component parent;

    public WidgetDialogCommand()
    {
        super();
    }

    public WidgetDialogCommand(String id)
    {
        super();
        setId(id);
    }

    protected void doExecuteCommand()
    {
        dialog = (dialog == null) ? createDialog() : dialog;
        if (getParent() != null)
        {
            dialog.setParentComponent(getParent());
        }
        dialog.showDialog();
    }

    protected ApplicationDialog createDialog()
    {
        ApplicationDialog newlyCreatedDialog = new TitledWidgetApplicationDialog(getWidget());
        ((ApplicationObjectConfigurer) Application.services().getService(ApplicationObjectConfigurer.class))
                .configure(newlyCreatedDialog, getId());
        return newlyCreatedDialog;
    }

    public Component getParent()
    {
        return parent;
    }

    /**
     * @param dialogParent
     *            The parent of the dialog for preservation of hierarchy and correct modality.
     */
    public void setParent(Component dialogParent)
    {
        this.parent = dialogParent;
    }
}

