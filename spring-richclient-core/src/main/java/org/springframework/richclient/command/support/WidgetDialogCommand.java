package org.springframework.richclient.command.support;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.TitledWidgetApplicationDialog;

import java.awt.*;

/**
 * <p>
 * WidgetDialogCommand toont een specifieke widget in een dialog.
 * </p>
 * <p>
 * Gebruik {@link #setParent(java.awt.Component)} om de parent van de dialoog correct te
 * zetten. Indien niet gegeven zal de applicatieWindow gebruikt worden. Dit zal
 * in veel gevallen correct zijn <bold>MAAR</bold> indien de hierarchie van
 * dialogen/windows niet correct is kan door de modaliteit van dialogen je
 * scherm blokkeren.
 * </p>
 * <p>
 * bv: <code>Window->dialoog1->dialoog2</code> Als dialoog2 geen correct
 * parent heeft gezet, zal na het sluiten van dialoog2 de focus naar Window
 * worden verschoven terwijl dialoog1 door modaliteit nog steeds boven het
 * Window staat en geen access toelaat op Window. Gevolg kan zijn dat je dus
 * niets meer kan doen.
 * </p>
 */
public class WidgetDialogCommand extends AbstractWidgetCommand
{
    /** ApplicationDialog waarop de widget zal terecht komen. */
    private ApplicationDialog dialog;

    /** parent voor centreren van dialog. */
    private Component parent;

    /**
     * Standaard constructor om een specifieke widget te tonen in een dialoog
     */
    public WidgetDialogCommand()
    {
        super();
    }

    /**
     * Standaard constructor om een specifieke widget te tonen in een dialoog
     * met met een specifieke id
     *
     * @param id
     *            De id van de dialoog
     */
    public WidgetDialogCommand(String id)
    {
        super();
        setId(id);
    }

    /**
     * Voert het commando uit. Toont standaard de dialoog gecentreerd op de
     * parent, of gecentreerd op het huidige scherm indien geen parent is
     * ingesteld.
     */
    protected void doExecuteCommand()
    {
        dialog = (dialog == null) ? createDialog() : dialog;
        if (getParent() != null)
        {
            dialog.setParentComponent(getParent());
        }
        dialog.showDialog();
    }

    /**
     * Cre\u00EBrt standaard een nieuwe TitledWidgetApplicationDialog.
     */
    protected ApplicationDialog createDialog()
    {
        ApplicationDialog newlyCreatedDialog = new TitledWidgetApplicationDialog(getWidget());
        ((ApplicationObjectConfigurer) Application.services().getService(ApplicationObjectConfigurer.class))
                .configure(newlyCreatedDialog, getId());
        return newlyCreatedDialog;
    }

    /**
     * @return De parent van de dialog voor behoud in hierarchie en correcte
     *         modaliteit.
     */
    public Component getParent()
    {
        return parent;
    }

    /**
     * @param dialogParent
     *            De parent van de dialog voor behoud in hierarchie en correcte
     *            modaliteit.
     */
    public void setParent(Component dialogParent)
    {
        this.parent = dialogParent;
    }
}

