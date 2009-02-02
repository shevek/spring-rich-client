package org.springframework.richclient.command.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.richclient.widget.Widget;

import javax.swing.*;

/**
 * Base class voor commando's die widgets gebruiken. Het widget kan worden
 * ge\u00EFnjecteerd of je kan het beanId meegeven dat gebruikt moet worden voor
 * het opzoeken in de context.
 */
public abstract class AbstractWidgetCommand extends ApplicationWindowAwareCommand
        implements
        ApplicationContextAware
{

    /** BeanId voor opzoeken in de context. */
    private String widgetBeanId = null;

    /** Widget voor dit command. */
    private Widget widget;

    /** Eventuele ActionClusters nodig om dit commando op te roepen. */
    private String actionCluster;

    /**
     * ApplicationContext die nodig is voor het eventuele opzoeken van het
     * widget.
     */
    private ApplicationContext applicationContext;

    /**
     * Widget factory getriggered door dit command.
     *
     * @param widget
     */
    public void setWidget(Widget widget)
    {
        this.widget = widget;
    }

    /**
     * @return het Widget voor dit command.
     */
    protected Widget getWidget()
    {
        // mss moet het widget nog opgezocht worden via zijn beanId
        if (this.widget == null && this.widgetBeanId != null)
            this.widget = RcpSupport.getBean(widgetBeanId);
        return this.widget;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * @return applicationContext gebruikt voor het opzoeken van de Widget.
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Het grafisch component geproduceerd door de onderliggende widget
     */
    protected final JComponent getWidgetComponent()
    {
        if (getWidget() == null)
            return RcpSupport.createDummyPanel("No widget set for command:" + getId());
        return getWidget().getComponent();
    }

    /**
     * @return id waarmee het widget is opgezocht of <code>null</code> indien
     *         het widget werd geinjecteerd.
     */
    public String getWidgetBeanId()
    {
        return widgetBeanId;
    }

    /**
     * Voorziening om beanId te zetten. Indien de command wordt
     * gedefini\u00eberd in een context xml, wordt de property Widget geresolved
     * naar een bean en dus geinstanti\u00eberd. Indien je de widgetBeanId
     * echter meegeeft, kan dit uitegesteld worden tot het oproepen van de
     * commando.
     *
     * @param widgetBeanId
     */
    public void setWidgetBeanId(String widgetBeanId)
    {
        this.widgetBeanId = widgetBeanId;
    }

    /**
     * De nodige actionClusters voor het uitvoeren van dit commando.
     *
     * @return String met actionClusters.
     */
    public String getActionCluster()
    {
        return this.actionCluster;
    }

    /**
     * Geef de actionClusters mee die moeten aanwezig zijn om dit commando te
     * kunnen uitvoeren. Impliciet wordt ook het securityControllerId gezet
     * omdat deze wordt gebruikt voor het configureren van het security aspect.
     * Deze id staat vast op "actionClusterController".
     *
     * @param actionCluster
     *            de nodige clusters voor het uitvoeren.
     */
    public void setActionCluster(String actionCluster)
    {
        this.actionCluster = actionCluster;
        setSecurityControllerId("actionClusterController");
    }
}

