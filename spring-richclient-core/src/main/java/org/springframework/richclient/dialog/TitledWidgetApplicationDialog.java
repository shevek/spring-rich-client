package org.springframework.richclient.dialog;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.DescriptionConfigurable;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.core.TitleConfigurable;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.widget.SelectionWidget;
import org.springframework.richclient.widget.TitledWidget;
import org.springframework.richclient.widget.Widget;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * Laat toe om een applicationDialog aan te maken waaraan de widget die getoond
 * moet worden, kan meegegeven worden.
 *
 * <p>
 * Niet vergeten om de parentComponent ({@link #setParentComponent(java.awt.Component)})
 * mee te geven indien het niet je applicatiewindow zelf is!
 * </p>
 */
public class TitledWidgetApplicationDialog extends ApplicationDialog
        implements
        DescriptionConfigurable,
            Messagable
{

    /** Default Id voor ok command. */
    public static final String OK_COMMAND_ID = "okCommand";

    /** Default Id voor cancel command. */
    public static final String CANCEL_COMMAND_ID = "cancelCommand";

    /** Default Id voor exit command. */
    public static final String EXIT_COMMAND_ID = "exit";

    /** Default Id voor select command. */
    public static final String SELECT_COMMAND_ID = "select";

    /** Default Id voor select command. */
    public static final String SELECT_NONE_COMMAND_ID = "selectNoneCommand";

    /** Ok-mode: enkel ok knop met finish command. */
    public static final int OK_MODE = 1;

    /** Cancel-mode: enkel cancel knop met cancel command. */
    public static final int CANCEL_MODE = 2;

    /** Select-mode: select en cancel knop met beide: finish en cancel command. */
    public static final int SELECT_CANCEL_MODE = 3;

    /** Widget dat als content in deze dialoog wordt weergegeven. */
    private final Widget widget;

    /** De mode voor deze dialoog (<code>OK_MODE, CANCEL_MODE of SELECT_CANCEL_MODE</code>) */
    private final int mode;

    /** Specifiek Id voor finish command. */
    private final String finishId;

    /** Specifiek Id voor cancel command. */
    private final String cancelId;

    /** Id voor het configureren van de dialoog. */
    private final String titledWidgetId;

    /** Selecteer niets commando. */
    private ActionCommand selectNoneCommand;

    /**
     * Aanmaken dialoog met enkel een ok-knop.
     *
     * @param widget
     *            te tonen widget.
     */
    public TitledWidgetApplicationDialog(Widget widget)
    {
        this(widget, OK_MODE);
    }

    /**
     * Aanmaak dialoog met gegeven mode.
     *
     * @param widget
     *            te tonen widget.
     * @param mode
     *            de mode van deze dialoog:
     *            <code>OK_MODE, CANCEL_MODE of SELECT_CANCEL_MODE</code>.
     */
    public TitledWidgetApplicationDialog(Widget widget, int mode)
    {
        // 1 knop geeft exit alleen, twee knoppen geeft select/exit, kan
        // aangepast worden met andere constructor
        this(widget, mode, mode == SELECT_CANCEL_MODE ? SELECT_COMMAND_ID : EXIT_COMMAND_ID, EXIT_COMMAND_ID);
    }

    /**
     * Aanmaak van dialoog met volledige configuratie.
     *
     * @param widget
     *            te tonen wigdet.
     * @param mode
     *            de mode van deze dialoog:
     *            <code>OK_MODE, CANCEL_MODE of SELECT_CANCEL_MODE</code>.
     * @param finishId
     *            specifiek id voor het finish commando.
     * @param cancelId
     *            specifiek id voor het cancel commando.
     */
    public TitledWidgetApplicationDialog(Widget widget, int mode, String finishId, String cancelId)
    {
        this.widget = widget;
        this.mode = mode;
        this.finishId = finishId;
        this.cancelId = cancelId;
        if (widget instanceof TitledWidget)
            this.titledWidgetId = ((TitledWidget) widget).getId();
        else
            this.titledWidgetId = null;
    }

    /**
     * @return widget van deze dialoog.
     */
    public Widget getWidget()
    {
        return this.widget;
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent createButtonBar()
    {
        CommandGroup widgetCommands = CommandGroup.createCommandGroup(null, widget.getCommands());
        CommandGroup dialogCommands = CommandGroup.createCommandGroup(getCommandGroupMembers());
        JPanel panel = new JPanel(new FormLayout(new ColumnSpec[]{FormFactory.DEFAULT_COLSPEC,
                FormFactory.GLUE_COLSPEC, FormFactory.UNRELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC},
                new RowSpec[]{FormFactory.DEFAULT_ROWSPEC}));
        CellConstraints cc = new CellConstraints();
        panel.add(widgetCommands.createButtonBar(), cc.xy(1, 1));
        panel.add(dialogCommands.createButtonBar(), cc.xy(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return panel;
    }

    /**
     * Aantal en welke commando's hangt af van mode.
     *
     * {@inheritDoc}
     */
    protected Object[] getCommandGroupMembers()
    {
        if (this.mode == SELECT_CANCEL_MODE)
            return new Object[]{getFinishCommand(), getSelectNoneCommand(), getCancelCommand()};
        if (this.mode == OK_MODE)
            return new Object[]{getFinishCommand()};
        if (this.mode == CANCEL_MODE)
            return new Object[]{getCancelCommand()};
        return new Object[]{getCancelCommand()};
    }

    /**
     * Specific command to de-select all and return a <code>null</code> selection.
     *
     * @return an {@link ActionCommand}.
     */
    protected ActionCommand getSelectNoneCommand()
    {
    	if (selectNoneCommand == null)
    	{
    		selectNoneCommand = new ActionCommand(getSelectNoneCommandId()) {
    			public void doExecuteCommand() {
    				onSelectNone();
    			}
    		};
    		selectNoneCommand.setSecurityControllerId(getFinishSecurityControllerId());
    	}
    	return selectNoneCommand;
    }

    private String getSelectNoneCommandId() {
		return SELECT_NONE_COMMAND_ID;
	}

	/**
     * {@inheritDoc}
     */
    protected void addDialogComponents()
    {
        JComponent dialogContentPane = createDialogContentPane();
        if (getPreferredSize() != null)
        {
            dialogContentPane.setSize(getPreferredSize());
        }
        if (!(this.widget instanceof TitledWidget))
        {
            GuiStandardUtils.attachDialogBorder(dialogContentPane);
        }
        getDialogContentPane().add(dialogContentPane);
        getDialogContentPane().add(createButtonBar(), BorderLayout.SOUTH);
        if (this.titledWidgetId != null)
            ((ApplicationObjectConfigurer) Application.services().getService(
                    ApplicationObjectConfigurer.class)).configure(this.widget, this.titledWidgetId);
    }

    /**
     * {@inheritDoc}
     */
    protected void onAboutToShow()
    {
        super.onAboutToShow();
        if (this.mode == SELECT_CANCEL_MODE && widget instanceof SelectionWidget)
            ((SelectionWidget) widget).setSelectionCommand(getFinishCommand());
        widget.onAboutToShow();
    }

    /**
     * {@inheritDoc}
     */
    protected void onWindowClosing()
    {
        widget.onAboutToHide();
        if (this.mode == SELECT_CANCEL_MODE && widget instanceof SelectionWidget)
            ((SelectionWidget) widget).removeSelectionCommand();
        super.onWindowClosing();
    }

    /**
     * {@inheritDoc}
     */
    protected boolean onFinish()
    {
        return true;
    }

	/**
	 * Hook called upon executing the select none command. This should normally
	 * de-select anything and execute the finish behaviour.
	 *
	 * @return
	 */
    protected boolean onSelectNone()
    {
		getFinishCommand().execute();
		return true;
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent createDialogContentPane()
    {
        return widget.getComponent();
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(String title)
    {
        super.setTitle(title);
        if ((this.widget instanceof TitleConfigurable) && (this.titledWidgetId == null))
            ((TitleConfigurable) this.widget).setTitle(title);
    }

    /**
     * {@inheritDoc}
     */
    protected String getFinishCommandId()
    {
        return this.finishId;
    }

    /**
     * {@inheritDoc}
     */
    protected String getCancelCommandId()
    {
        return this.cancelId;
    }

    /**
     * {@inheritDoc}
     */
    public void setCaption(String shortDescription)
    {
        if (this.widget instanceof DescriptionConfigurable)
            ((DescriptionConfigurable) this.widget).setCaption(shortDescription);
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String longDescription)
    {
        if (this.widget instanceof DescriptionConfigurable)
            ((DescriptionConfigurable) this.widget).setDescription(longDescription);
    }

    /**
     * {@inheritDoc}
     */
    public void setMessage(Message message)
    {
        if (this.widget instanceof Messagable)
            ((Messagable) this.widget).setMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        if (this.widget instanceof Messagable)
            ((Messagable) this.widget).addPropertyChangeListener(propertyChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener propertyChangeListener)
    {
        if (this.widget instanceof Messagable)
            ((Messagable) this.widget).addPropertyChangeListener(property, propertyChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener)
    {
        if (this.widget instanceof Messagable)
            ((Messagable) this.widget).removePropertyChangeListener(propertyChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String property, PropertyChangeListener propertyChangeListener)
    {
        if (this.widget instanceof Messagable)
            ((Messagable) this.widget).removePropertyChangeListener(property, propertyChangeListener);
    }
}
