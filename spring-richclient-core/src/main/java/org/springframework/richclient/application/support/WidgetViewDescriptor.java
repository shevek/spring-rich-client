package org.springframework.richclient.application.support;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.widget.Widget;
import org.springframework.richclient.widget.WidgetView;

public final class WidgetViewDescriptor extends DefaultViewDescriptor
{
    /**
     * Widget to create the view.
     */
    private Widget widget;
    
    public WidgetViewDescriptor(String id, Widget widget)
    {
        setId(id);
        this.widget = widget;
    }

    /**
     * {@inheritDoc}
     */
    public PageComponent createPageComponent()
    {
        AbstractView sv = new WidgetView(this.widget);
        sv.setDescriptor(this);
        return sv;
    }
}