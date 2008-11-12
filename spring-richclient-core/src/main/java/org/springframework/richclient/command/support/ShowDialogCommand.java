package org.springframework.richclient.command.support;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.util.RcpSupport;

/**
 * Command that shows an application dialog
 *
 * @author ldo
 */
public class ShowDialogCommand extends ActionCommand
{

    private final ApplicationDialog dialog;
    private String actionCluster;

    public ShowDialogCommand(String id, ApplicationDialog dialog)
    {
        super(id);
        this.dialog = dialog;
        RcpSupport.configure(this);
    }

    @Override
    protected void doExecuteCommand()
    {
        dialog.showDialog();
    }

    public String getActionCluster()
    {
        return actionCluster;
    }

    public void setActionCluster(String actionCluster)
    {
        this.actionCluster = actionCluster;

    }
}