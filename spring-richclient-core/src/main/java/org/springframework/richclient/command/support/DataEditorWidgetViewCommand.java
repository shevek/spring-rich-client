package org.springframework.richclient.command.support;

import org.springframework.richclient.widget.Widget;
import org.springframework.richclient.widget.editor.AbstractDataEditorWidget;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Widget command om een DefaultDataEditorWidget te tonen (of een widget
 * die daarvan extend)
 *
 * @author ldo
 * @since 0.4.4
 */
public class DataEditorWidgetViewCommand extends WidgetViewCommand
{
    /**
     * {@inheritDoc}
     *
     * Open de dataeditor.
     */
    protected void doExecuteCommand()
    {
        Widget widget = super.getWidget();
        Assert.isInstanceOf(AbstractDataEditorWidget.class, widget);
        AbstractDataEditorWidget dataEditorWidget = (AbstractDataEditorWidget)widget;
        Object dataEditorParameters = getParameter(DefaultDataEditorWidget.PARAMETER_MAP);
        if(dataEditorParameters != null)
        {
            dataEditorWidget.executeFilter((Map<String, Object>)dataEditorParameters);
        }

        super.doExecuteCommand();
    }
}

