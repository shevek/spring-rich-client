/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.selection.binding.support;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.binding.value.PropertyChangePublisher;
import org.springframework.binding.value.support.PropertyChangeSupport;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.config.CommandConfigurer;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.factory.AbstractControlFactory;

/**
 * <tt>SelectField</tt> base class. Allows for customization of the renderer component.
 * <p>
 * A <tt>SelectField</tt> provides a renderer component (provided by subclasses), a select button and a clear button.
 * The renderer component shows the current value, the select button opens the selection dialog, and the clear button
 * sets the value to <code>null</code>.
 * </p>
 * 
 * @author Peter De Bruycker
 */
public abstract class SelectField extends AbstractControlFactory implements PropertyChangePublisher {

    private JComponent renderer;
    private SelectCommand selectCommand = new SelectCommand();
    private ClearCommand clearCommand = new ClearCommand();
    private boolean editable;
    private Object value;
    private ApplicationDialog dialog;
    private LabelProvider labelProvider;
    private JPanel control;
    private boolean nullable = true;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected JComponent createControl() {
        control = new JPanel(new BorderLayout());

        renderer = createRenderer();
        control.add(renderer);

        // configure commands
        CommandConfigurer configurer = (CommandConfigurer) ApplicationServicesLocator.services().getService(
                CommandConfigurer.class);
        configurer.configure(selectCommand);
        configurer.configure(clearCommand);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

        buttonPanel.add(selectCommand.createButton());
        if (nullable) {
            buttonPanel.add(clearCommand.createButton());
        }

        control.add(buttonPanel, BorderLayout.LINE_END);

        return control;
    }

    /**
     * Create the component that will do the rendering. Cannot return <code>null</code>.
     * 
     * @return the renderer component
     */
    protected abstract JComponent createRenderer();

    public void setEnabled(boolean enabled) {
        control.setEnabled(enabled);

        renderer.setEnabled(enabled);
        selectCommand.setEnabled(enabled);
        clearCommand.setEnabled(enabled);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;

        selectCommand.setEnabled(control.isEnabled() && editable);
        clearCommand.setEnabled(control.isEnabled() && editable);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;

        render(value);

        propertyChangeSupport.firePropertyChange("value", oldValue, value);
    }

    /**
     * Convenience method, calls <code>setValue(null)</code>.
     */
    public void clear() {
        setValue(null);
    }

    /**
     * Render the given value. Warning: the value can be <code>null</code>.
     * 
     * @param value
     *            the value
     */
    protected abstract void render(Object value);

    public Object getValue() {
        return value;
    }

    public void setSelectionDialog(ApplicationDialog dialog) {
        this.dialog = dialog;
    }

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public LabelProvider getLabelProvider() {
        return labelProvider;
    }

    /**
     * Returns whether the property is nullable. If set to true, the "clear" button is shown.
     * 
     * @return whether the property is nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets whether the clear button is shown
     * 
     * @param nullable
     *            whether the clear button is shown
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    private class ClearCommand extends ActionCommand {

        public ClearCommand() {
            super("selectField.clearCommand");
        }

        protected void doExecuteCommand() {
            clear();
        }

    }

    private class SelectCommand extends ActionCommand {

        public SelectCommand() {
            super("selectField.selectCommand");
        }

        protected void doExecuteCommand() {
            dialog.showDialog();
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }
}
