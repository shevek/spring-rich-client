package org.springframework.richclient.form;

import org.springframework.richclient.application.config.ApplicationObjectConfigurer;
import org.springframework.richclient.components.Focussable;
import org.springframework.richclient.widget.AbstractWidgetForm;
import org.springframework.richclient.util.RcpSupport;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;

import javax.swing.*;
import java.awt.*;

/**
 * Form implementation for the Focussable interface.
 *
 * @author Jan Hoskens
 *
 */
public abstract class AbstractFocussableForm extends AbstractWidgetForm implements Focussable//, SecurityControllable
{

    public static final String UNSAVEDCHANGES_WARNING_ID = "unsavedchanges.warning";

    public static final String UNSAVEDCHANGES_HASERRORS_WARNING_ID = "unsavedchanges.haserrors.warning";

    private JComponent focusControl;

    private final Runnable focusRequestRunnable = new Runnable()
    {

        public void run()
        {
            if (focusControl != null)
                focusControl.requestFocusInWindow();
        }
    };

    /**
     * Override to do nothing. Superclass registers a default command, but we are using a different system to
     * define default commands.
     */
    @Override
    protected void handleEnabledChange(boolean enabled)
    {
    }

    /**
     * Registers the component that receives the focus when the form receives focus.
     *
     * @see #grabFocus
     */
    public void setFocusControl(JComponent field)
    {
        this.focusControl = field;
    }

    public void grabFocus()
    {
        if (this.focusControl != null)
            EventQueue.invokeLater(focusRequestRunnable);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm()
    {
        super();
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(FormModel formModel, String formId)
    {
        super(formModel, formId);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(FormModel pageFormModel)
    {
        super(pageFormModel);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(HierarchicalFormModel parentFormModel, String formId,
            String childFormObjectPropertyPath)
    {
        super(parentFormModel, formId, childFormObjectPropertyPath);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(HierarchicalFormModel parentFormModel, String formId,
            ValueModel childFormObjectHolder)
    {
        super(parentFormModel, formId, childFormObjectHolder);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(HierarchicalFormModel parentFormModel, String formId)
    {
        super(parentFormModel, formId);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(Object formObject)
    {
        super(formObject);
    }

    /**
     * @inheritDoc
     */
    public AbstractFocussableForm(String formId)
    {
        super(formId);
    }

    public boolean canClose()
    {
        boolean userBreak = false;
        int answer = JOptionPane.NO_OPTION; // by default no save is required.

        // unless of course there are unsaved changes and we can commit (isAuthorized)
        if (this.getFormModel().isEnabled() && this.getFormModel().isDirty()
                && this.getCommitCommand().isAuthorized())
        { // then we ask the user to save the mess first: yes/no/cancel
            answer = RcpSupport.showWarningDialog(this.getControl(), UNSAVEDCHANGES_WARNING_ID,
                    JOptionPane.YES_NO_CANCEL_OPTION);

            switch (answer)
            {
                case JOptionPane.CANCEL_OPTION :
                    // backup the selection change so table and detail keep in sync
                    // gives problems (asks unsavedchanges twice)
                    userBreak = true;
                    break;
                case JOptionPane.YES_OPTION :
                    if (this.getFormModel().getHasErrors() == true)
                    {
                        RcpSupport.showWarningDialog(this.getControl(), UNSAVEDCHANGES_HASERRORS_WARNING_ID);
                        userBreak = true;
                        break;
                    }
                    this.getCommitCommand().execute();
                    break;
                case JOptionPane.NO_OPTION :
                {
                    this.revert(); // revert so no strange things happen (hopefully)
                    break;
                }
            }
        }

        return !userBreak;
    }

    @Override
    protected void init()
    {
        // eerst wordt parent object constructor opgeroepen waarin deze init
        // wordt opgeroepen. Gewone fields zijn dan nog niet gezet dus doe dat
        // hier expliciet ipv in velddeclaratie.
//        authorized = true;
//        enabled = true;
//        setSecurityControllerId(getId() + ".authorize");
        ((ApplicationObjectConfigurer) getApplicationServices().getService(ApplicationObjectConfigurer.class))
                .configure(this, getId());
    }
}

