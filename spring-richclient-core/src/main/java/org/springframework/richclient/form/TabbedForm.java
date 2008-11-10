package org.springframework.richclient.form;

import org.springframework.util.Assert;
import org.springframework.binding.form.VetoableCommitListener;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.NewFormObjectAware;
import org.springframework.richclient.components.MessagableTabbedPane;
import org.springframework.richclient.components.SkipComponentsFocusTraversalPolicy;
import org.springframework.richclient.util.RcpSupport;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public abstract class TabbedForm extends AbstractFocussableForm implements ChangeListener, NewFormObjectAware
{

    private List<VetoableCommitListener> vetoableCommitListeners;

    private JTabbedPane tabbedPane = null;

    public TabbedForm(Object formObject, String formId)
    {
        this(FormModelHelper.createFormModel(formObject), formId);
    }

    public TabbedForm(FormModel formModel, String formId)
    {
        super(formModel, formId);

        if (this.getFormModel().getId() == null)
            this.getFormModel().setId(formId);
    }

    public TabbedForm(FormModel formModel)
    {
        this(formModel, formModel.getId());
    }

    protected JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }

    protected final JComponent createFormControl()
    {
        tabbedPane = new MessagableTabbedPane(SwingConstants.TOP);
        tabbedPane.setFocusTraversalPolicyProvider(true);
        tabbedPane
                .setFocusTraversalPolicy(SkipComponentsFocusTraversalPolicy.skipJTextComponentTraversalPolicy);
        for (TabbedForm.Tab tab : getTabs())
        {
            tab.setParent(tabbedPane);
        }
        tabbedPane.addChangeListener(this);
        return tabbedPane;
    }

    public JComponent getRevertComponent()
    {
        return getRevertCommand().createButton();
    }

    public void setFormObject(Object formObject)
    {
        if (formObject == null)
            selectTab(0);
        super.setFormObject(formObject);
    }

    public void setNewFormObject(Object formObject)
    {
        if (formObject != null)
        {
            super.setFormObject(formObject);
        }
        else
        {
            getNewFormObjectCommand().execute();
        }
        selectTab(0);
    }

    public void selectTab(int tabIndex)
    {
        if ((tabbedPane != null) && (tabbedPane.getTabCount() > tabIndex))
            tabbedPane.setSelectedIndex(tabIndex);
    }

    public void selectTab(Tab tab)
    {
        if (tab.getTabIndex() > 0)
        {
            tabbedPane.setSelectedIndex(tab.getTabIndex());
        }
    }

    protected abstract Tab[] getTabs();

    protected class Tab
    {

        private final String tabId;

        private final String title;

        private final JComponent panel;

        private FocusTraversalPolicy focusTraversalPolicy;

        private JTabbedPane parentPane;

        private int tabIndex = -1;

        private boolean enabled = true;

        private boolean visible = true;

        public Tab(String tabId, JComponent panel)
        {
            Assert.notNull(panel);
            this.tabId = tabId;
            this.title = RcpSupport.getMessage(getId(), this.tabId, RcpSupport.TITLE);
            this.panel = panel;
            this.panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        /**
         * Bijhouden parent voor markeringen/enablen.
         *
         * @param parentPane
         */
        protected void setParent(JTabbedPane parentPane)
        {
            this.parentPane = parentPane;
            if (this.parentPane != null)
                setVisible(visible);
        }

        /**
         * Tab verwijderen/toevoegen aan de tabbedpane.
         *
         * @param visible
         */
        public void setVisible(boolean visible)
        {
            if (parentPane != null)
            {
                if (visible)
                {
                    parentPane.addTab(title, panel);
                    tabIndex = parentPane.indexOfComponent(panel);
                    parentPane.setEnabledAt(tabIndex, isEnabled());
                }
                else
                {
                    parentPane.remove(panel);
                    tabIndex = -1;
                }
            }
            this.visible = visible;
        }

        /**
         * Enabled aan/uitzetten, zal ook de visuele tab enablen indien aangemaakt.
         *
         * @param enabled
         */
        public void setEnabled(boolean enabled)
        {
            if ((parentPane != null) && (tabIndex > -1))
                parentPane.setEnabledAt(tabIndex, enabled);

            this.enabled = enabled;
        }

        /**
         * Geef de index van de component op de tabbedPane.
         *
         * @return index van het tabblad. -1 indien niet visible.
         */
        public int getTabIndex()
        {
            return tabIndex;
        }

        /**
         * @return <code>true</code> indien enabled.
         */
        public boolean isEnabled()
        {
            return this.enabled;
        }

        /**
         * Aanduiden of het tabblad gemarkeerd moet worden of niet. Dit zal meestal het geval zijn als er nog
         * in te vullen verplichte velden op het tabblad staan.
         *
         * @param enable
         */
        public void setMarked(boolean enable)
        {
            // tabbladen met verplichte velden aanduiden met een icoontje
            Icon icon = RcpSupport.getIcon(tabId + ".icon");
            if ((parentPane != null) && (tabIndex > -1))
                parentPane.setIconAt(getTabIndex(), enable ? icon : null);

        }

        /**
         * Elke tab kan een specifieke focusTraversalPolicy hebben.
         *
         * @param focusTraversalPolicy
         */
        public void setFocusTraversalPolicy(FocusTraversalPolicy focusTraversalPolicy)
        {
            this.focusTraversalPolicy = focusTraversalPolicy;
            panel.setFocusTraversalPolicy(this.focusTraversalPolicy);
            panel.setFocusTraversalPolicyProvider(this.focusTraversalPolicy == null ? false : true);
        }
    }

    @Override
    public void commit()
    {
        FormModel formModel = getFormModel();
        if (vetoableCommitListeners != null)
        {
            for (VetoableCommitListener v : vetoableCommitListeners)
            {
                if (!v.proceedWithCommit(formModel))
                    return;
            }
        }
        super.commit();
    }

    /**
     * Opgeroepen wanneer er iets aan het tabbedPane verandert. Overschrijven indien er moet gereageerd worden
     * op deze veranderingen.
     */
    public void stateChanged(ChangeEvent e)
    {
    }

    /**
     * Adding a vetoableCommitListener might prevent a formModel.commit() but this is not the correct location
     * to add back-end logic to check for a consistent formObject. Besides this the vetoableCommitListener
     * doesn't add any other real advantage for our case. Therefor deprecating to prevent wrong usage.
     */
    @Deprecated
    public void addVetoableCommitListener(VetoableCommitListener vetoableCommitListener)
    {
        if (vetoableCommitListeners == null)
            vetoableCommitListeners = new ArrayList<VetoableCommitListener>(5);
        vetoableCommitListeners.add(vetoableCommitListener);
    }

    @Deprecated
    public void removeVetoableCommitListener(VetoableCommitListener vetoableCommitListener)
    {
        if (vetoableCommitListeners != null)
            vetoableCommitListeners.remove(vetoableCommitListener);
    }
}
