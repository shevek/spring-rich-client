package org.springframework.richclient.samples.dataeditor.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;
import org.springframework.richclient.application.splash.MonitoringSplashScreen;
import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.richclient.util.WindowUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class DataEditorSplash implements MonitoringSplashScreen
{
    private JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(180,180));
    private JXLabel titleLabel = new JXLabel("Data Editor");
    private JProgressBar progressBar = new JProgressBar();
    private JXLabel progressLabel = new JXLabel();
    private JXFrame frame;

    public DataEditorSplash()
    {
        BusyPainter painter = new BusyPainter(
        new RoundRectangle2D.Float(0, 0,28.0f,8.6f,10.0f,10.0f),
        new Ellipse2D.Float(15.0f,15.0f,70.0f,70.0f));
        painter.setTrailLength(4);
        painter.setPoints(8);
        painter.setFrame(7);
        painter.setHighlightColor(new Color(30,42,102));
        busyLabel.setPreferredSize(new Dimension(100,100));
        busyLabel.setIcon(new EmptyIcon(100,100));
        busyLabel.setBusyPainter(painter);

        titleLabel.setFont(titleLabel.getFont().deriveFont(30f));
        titleLabel.setForeground(Color.white);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(170,20));
//        titleLabel.setBackground(new Color(0x425DA9));
//        titleLabel.setOpaque(false);
//        progressLabel.setBackground(new Color(0x425DA9));
    }

    public ProgressMonitor getProgressMonitor()
    {
         return new ProgressMonitor()
         {
             private int currentWork = 0;

             public void taskStarted(String name, int totalWork)
             {
                 progressBar.setMaximum(totalWork);
                 progressBar.setValue(0);
                 this.currentWork = 0;
                 busyLabel.setBusy(true);
                 progressBar.setString(name);
             }

             public void subTaskStarted(String name)
             {
                 progressBar.setString(name);
             }

             public void worked(int work)
             {
                 currentWork += work;
                 progressBar.setValue(currentWork);
             }

             public void done()
             {
                 busyLabel.setBusy(false);
             }

             public boolean isCanceled()
             {
                 return false;
             }

             public void setCanceled(boolean b)
             {
             }
         };
    }

    protected Component createContentPane()
    {
        JXPanel panel = new JXPanel(new FormLayout("center:200px:nogrow, left:3dlu:nogrow, fill:200px:nogrow", "center:200px:nogrow, center:20px:nogrow"));
        panel.setBackground(new Color(0x425DA9));
        panel.add(busyLabel, new CellConstraints(1,1));
        JXPanel panel2 = new JXPanel(new FormLayout("center:195px:nogrow", "center:98px:nogrow, center:4dlu:nogrow, center:98px:nogrow"));
        panel2.setBackground(new Color(0x425DA9));
        panel2.add(titleLabel, new CellConstraints(1, 1));
        panel.add(progressBar, new CellConstraints(1, 2, 3, 1));
        panel.add(panel2, new CellConstraints(3,1));
        panel.setBorder(BorderFactory.createLineBorder(Color.black, 4));
        return panel;
    }

    public void splash()
    {
        frame = new JXFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
        frame.setRootPane(new JXRootPane());
        frame.getRootPaneExt().setDoubleBuffered(true);
        frame.getRootPaneExt().setBackground(new Color(255,255,255,255));
        frame.getRootPaneExt().setOpaque(false);
	    frame.getContentPane().add(createContentPane());
		frame.pack();
		WindowUtils.centerOnScreen(frame);
		frame.setVisible(true);
    }

    public void dispose()
    {
        if (frame != null) {
			frame.dispose();
			frame = null;
		}
    }
}
