package org.springframework.richclient.widget;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * View implementation to show a widget
 */
public class WidgetView extends AbstractView
{
    private Widget widget;

    public WidgetView()
    {
    }

    public WidgetView(Widget widget)
    {
        setWidget(widget);
    }

    public void setWidget(Widget widget)
    {
        this.widget = widget;
    }

    public Widget getWidget()
    {
        return this.widget;
    }

    protected JComponent createControl()
    {
        JComponent widgetComponent = getWidget().getComponent();
        JPanel viewPanel = new JPanel(new BorderLayout());
        viewPanel.add(widgetComponent, BorderLayout.CENTER);
        Widget widget = getWidget();
        List<? extends AbstractCommand> widgetCommands = widget.getCommands();
        if (widgetCommands != null)
        {
            JComponent widgetButtonBar = CommandGroup.createCommandGroup(widgetCommands).createButtonBar(ColumnSpec.decode("fill:pref:nogrow"), RowSpec.decode("fill:default:nogrow"), null);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.add(widgetButtonBar);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            viewPanel.add(buttonPanel, BorderLayout.SOUTH);
        }
        return viewPanel;
    }

    public boolean canClose()
    {
        return getWidget().canClose();
    }

    public void componentFocusGained()
    {
        getWidget().onAboutToShow();
    }

    public void componentFocusLost()
    {
        getWidget().onAboutToHide();
    }
}

