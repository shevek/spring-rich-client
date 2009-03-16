package org.springframework.richclient.command.support;

import org.springframework.richclient.application.support.WidgetViewDescriptor;

/**
 * Widget command that shows a widget in a view
 */
public class WidgetViewCommand extends AbstractWidgetCommand
{

    protected WidgetViewDescriptor widgetViewDescriptor;

    protected String widgetViewDescriptorId;

    /**
     * Shows the widget in the view
     */
    protected void doExecuteCommand()
    {
        if (this.widgetViewDescriptor == null)
            this.widgetViewDescriptor = createWidgetViewDescriptor();

        getApplicationWindow().getPage().showView(widgetViewDescriptor.getId());

    }

    public void setWidgetViewDescriptorId(String widgetViewDescriptorId)
    {
        this.widgetViewDescriptorId = widgetViewDescriptorId;
    }

    protected WidgetViewDescriptor createWidgetViewDescriptor()
    {
        if (this.widgetViewDescriptorId != null)
            return (WidgetViewDescriptor)getApplicationContext().getBean(this.widgetViewDescriptorId);

        return  new WidgetViewDescriptor(getId(), getWidget());
    }

    @Override
    public void setAuthorized(boolean authorized)
    {
        super.setAuthorized(authorized);
        if ((this.widgetViewDescriptor != null) && !authorized)
            if (this.widgetViewDescriptor.getId().equals(getApplicationWindow().getPage().getActiveComponent().getId()))
                getApplicationWindow().getPage().showView(null);
    }
}

