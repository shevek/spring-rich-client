package org.springframework.richclient.samples.dataeditor.app;

import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.taskpane.TaskPaneNavigatorApplicationLifecycleAdvisor;

import java.awt.*;
import java.util.Arrays;

public class DataEditorApplicationLifecycleAdvisor extends TaskPaneNavigatorApplicationLifecycleAdvisor
{
    private JXLoginDialog jxLoginDialog;

    @Override
    public void onWindowOpened(ApplicationWindow window)
    {
        super.onWindowOpened(window);
        window.getControl().setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    @Override
     public void onPreStartup()
    {
        jxLoginDialog = new JXLoginDialog(new LoginService()
        {
            @Override
            public boolean authenticate(String name, char[] password, String server) throws Exception
            {
                Thread.sleep(2000);
                return true;
            }
        }, null, null);
        jxLoginDialog.getPanel().setServers(Arrays.asList("Server1", "Server2"));
        jxLoginDialog.setModal(true);
        jxLoginDialog.setVisible(true);
        if(jxLoginDialog.getStatus() != JXLoginPane.Status.SUCCEEDED)
        {
            System.exit(1);
        }
    }

}
