package org.springframework.richclient.widget;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;

import javax.swing.*;
import java.util.List;
import java.util.Collections;

/**
 * Provides an easy way to create widgets based on an AbstractForm.
 */
public abstract class AbstractWidgetForm extends AbstractForm implements Widget
{
    /** Id for the undo command. */
    public static final String UNDO_CMD_ID = "undo";

    /** Id for the save command. */
    public static final String SAVE_CMD_ID = "save";

    protected boolean showing = false;

    protected AbstractWidgetForm() {
        super();
    }

    protected AbstractWidgetForm(String formId) {
        super(formId);
    }

    protected AbstractWidgetForm(Object formObject) {
        super(formObject);
    }

    protected AbstractWidgetForm(FormModel pageFormModel) {
        super(pageFormModel);
    }

    protected AbstractWidgetForm(FormModel formModel, String formId) {
        super(formModel, formId);
    }

    protected AbstractWidgetForm(HierarchicalFormModel parentFormModel, String formId, String childFormObjectPropertyPath) {
        super(parentFormModel, formId, childFormObjectPropertyPath);
    }

    protected AbstractWidgetForm(HierarchicalFormModel parentFormModel, String formId, ValueModel childFormObjectHolder) {
        super(parentFormModel, formId, childFormObjectHolder);
    }

    public boolean canClose()
    {
        return true;
    }

    public List<? extends AbstractCommand> getCommands()
    {
        return Collections.emptyList();
    }

    public JComponent getComponent()
    {
        return getControl();
    }

    public void onAboutToHide()
    {
        showing = false;
    }

    public void onAboutToShow()
    {
        showing = true;
    }

    public boolean isShowing()
    {
        return showing;
    }

    @Override
    protected String getCommitCommandFaceDescriptorId()
    {
        return SAVE_CMD_ID;
    }

    @Override
    protected String getRevertCommandFaceDescriptorId()
    {
        return UNDO_CMD_ID;
    }
}

