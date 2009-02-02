package org.springframework.richclient.command.support;

import org.springframework.richclient.application.support.WidgetViewDescriptor;

/**
 * WidgetViewCommand toont een specifieke widget in een view. Deze command vergt
 * een widget/widgetId of een widgetViewDescriptorId om de view correct te openen.
 */
public class WidgetViewCommand extends AbstractWidgetCommand
{

    /**
     * ViewDescriptor die bij dit commando hoort.
     */
    protected WidgetViewDescriptor widgetViewDescriptor;

    /**
     * Id van de descriptor indien geen widget aanwezig.
     */
    protected String widgetViewDescriptorId;

    /**
     * {@inheritDoc}
     *
     * Open de view.
     */
    protected void doExecuteCommand()
    {
        if (this.widgetViewDescriptor == null)
            this.widgetViewDescriptor = createWidgetViewDescriptor();

        getApplicationWindow().getPage().showView(widgetViewDescriptor.getId());

    }

    /**
     * Gebruik de gegeven widgetViewDescriptorId in plaats van een widget.
     *
     * @param widgetViewDescriptorId
     */
    public void setWidgetViewDescriptorId(String widgetViewDescriptorId)
    {
        this.widgetViewDescriptorId = widgetViewDescriptorId;
    }

    /**
     * Aanmaken van de ViewDescriptor.
     */
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

