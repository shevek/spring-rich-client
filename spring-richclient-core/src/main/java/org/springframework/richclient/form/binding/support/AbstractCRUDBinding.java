package org.springframework.richclient.form.binding.support;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.AbstractCommand;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractCRUDBinding extends CustomBinding
{

    /**
     * Commando voor verwijderen.
     */
    private AbstractCommand removeCommand;

    /**
     * Commando voor toevoegen.
     */
    private AbstractCommand addCommand;

    /**
     * Commando voor tonen van een detail.
     */
    private AbstractCommand detailCommand;

    /**
     * Commando voor editeren.
     */
    private AbstractCommand editCommand;

    /**
     * Alle beschikbare commando's.
     */
    private List<AbstractCommand>  commands;

    private boolean addSupported;

    private boolean removeSupported;

    private boolean editSupported;

    private boolean showDetailSupported;

    /**
     * Constructor.
     *
     * @param formModel
     *            Het formModel waarop deze binding werkt.
     * @param formPropertyPath
     *            Het pad naar de property.
     * @param requiredSourceClass
     *            Vereiste Type van object.
     */
    protected AbstractCRUDBinding(FormModel formModel, String formPropertyPath, Class requiredSourceClass)
    {
        super(formModel, formPropertyPath, requiredSourceClass);
    }

//    @Override
//    protected ValueModel getValueModel()
//    {
//        // hack, formModel should have some pluggable valuemodel
//        // selection/creation
//        if (valueModel == null)
//        {
//            if (formModel.isBuffered())
//            {
//                DefaultFormModel defaultFormModel = (DefaultFormModel) formModel;
//                valueModel = new CloneBufferedValueModel(defaultFormModel
//                        .getFormObjectPropertyAccessStrategy().getPropertyValueModel(formPropertyPath));
//                defaultFormModel.add(formPropertyPath, valueModel);
//            }
//            valueModel = formModel.getValueModel(formPropertyPath);
//        }
//        return valueModel;
//    }

    /**
     * @return de commando's die je op het scherm wilt zien
     */
    protected List<AbstractCommand> getCommands()
    {
        if (this.commands == null)
            this.commands = createCommands();
        return this.commands;
    }

    /**
     * Aanmaken van de verschillende commando's.
     *
     * @return een array met de beschikbare commando's.
     */
    protected List<AbstractCommand> createCommands()
    {
        int i = isAddSupported() ? 1 : 0;
        i = isRemoveSupported() ? i + 1 : i;
        i = isShowDetailSupported() ? i + 1 : i;
        i = isEditSupported() ? i + 1 : i;
        this.commands = new ArrayList<AbstractCommand>(i);
        if (isShowDetailSupported())
            commands.add(getDetailCommand());
        if (isAddSupported())
            commands.add(getAddCommand());
        if (isRemoveSupported())
            commands.add(getRemoveCommand());
        if (isEditSupported())
            commands.add(getEditCommand());
        return this.commands;
    }

    /**
     * @return AbstractCommand dat een rij toevoegd aan de tabel
     */
    protected AbstractCommand getAddCommand()
    {
        if (this.addCommand == null)
            this.addCommand = createAddCommand();
        return this.addCommand;
    }

    /**
     * @return AbstractCommand dat een rij toevoegd aan de tabel
     */
    abstract protected AbstractCommand createAddCommand();

    /**
     * @return AbstractCommand dat een rij verwijdert uit de tabel
     */
    protected AbstractCommand getRemoveCommand()
    {
        if (this.removeCommand == null)
            this.removeCommand = createRemoveCommand();
        return this.removeCommand;
    }

    /**
     * @return AbstractCommand dat een rij verwijdert uit de tabel
     */
    abstract protected AbstractCommand createRemoveCommand();

    /**
     * @return AbstractCommand dat meer info laat zien van de rij
     */
    protected AbstractCommand getDetailCommand()
    {
        if (this.detailCommand == null)
            this.detailCommand = createDetailCommand();
        return this.detailCommand;
    }

    /**
     * @return AbstractCommand dat meer info laat zien van de rij
     */
    abstract protected AbstractCommand createDetailCommand();

    /**
     * @return AbstractCommand dat een rij verandert uit de tabel.
     */
    protected AbstractCommand getEditCommand()
    {
        if (this.editCommand == null)
            this.editCommand = createEditCommand();
        return this.editCommand;
    }

    /**
     * @return AbstractCommand dat een rij verandert uit de tabel
     */
    abstract protected AbstractCommand createEditCommand();

    public boolean isAddSupported()
    {
        return addSupported;
    }

    public void setAddSupported(boolean addSupported)
    {
        this.addSupported = addSupported;
    }

    public boolean isEditSupported()
    {
        return editSupported;
    }

    public void setEditSupported(boolean editSupported)
    {
        this.editSupported = editSupported;
    }

    public boolean isRemoveSupported()
    {
        return removeSupported;
    }

    public void setRemoveSupported(boolean removeSupported)
    {
        this.removeSupported = removeSupported;
    }

    public boolean isShowDetailSupported()
    {
        return showDetailSupported;
    }

    public void setShowDetailSupported(boolean showDetailSupported)
    {
        this.showDetailSupported = showDetailSupported;
    }
}

