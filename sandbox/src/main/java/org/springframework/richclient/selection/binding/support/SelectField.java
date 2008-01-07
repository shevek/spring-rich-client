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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.selection.dialog.ListSelectionDialog;

/**
 * <tt>SelectField</tt> base class. Allows for customization of the renderer component.
 * <p>
 * A <tt>SelectField</tt> provides a renderer component (provided by subclasses), a select button and a clear button.
 * The renderer component shows the current value, the select button opens the selection dialog, and the clear button
 * sets the value to <code>null</code>.
 * </p>
 * 
 * TODO use icons on the buttons
 * 
 * @author Peter De Bruycker
 */
public abstract class SelectField extends JPanel {

    private JComponent renderer;
    private JButton selectButton;
    private JButton clearButton;

    private boolean editable;

    private Object value;

    private ListSelectionDialog dialog;

    private LabelProvider labelProvider;

    public SelectField() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        renderer = createRenderer();
        add(renderer);

        selectButton = new JButton("...");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.showDialog();
            }
        });
        add(selectButton);

        clearButton = new JButton("X");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setValue(null);
            }
        });
        add(clearButton);
    }

    /**
     * Create the component that will do the rendering. Cannot return <code>null</code>.
     * 
     * @return the renderer component
     */
    protected abstract JComponent createRenderer();

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        renderer.setEnabled(enabled);
        selectButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;

        selectButton.setEnabled(isEnabled() && editable);
        clearButton.setEnabled(isEnabled() && editable);
    }

    public boolean isEditable() {
        return editable;
    }

    public JButton getSelectButton() {
        return selectButton;
    }

    public void setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;

        render(value);

        firePropertyChange("value", oldValue, value);
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

    public void setSelectionDialog(ListSelectionDialog dialog) {
        this.dialog = dialog;
    }

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public LabelProvider getLabelProvider() {
        return labelProvider;
    }
}
