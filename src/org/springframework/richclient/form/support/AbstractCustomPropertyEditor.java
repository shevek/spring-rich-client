/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.form.support;

import java.awt.Component;
import java.beans.PropertyEditorSupport;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.FormAwarePropertyEditor;
import org.springframework.util.Assert;

/**
 * An abstract base class for property editors that provide a custom editor component.
 * <p>
 * Subclasses will need to provide implementations for the 
 * {@link #propertyValueChanged() <code>propertyValueChanged</code>}
 * and {@link #createCustomEditor() <code>createCustomEditor</code>} methods.
 * 
 * @author Oliver Hutchison
 */
public abstract class AbstractCustomPropertyEditor extends PropertyEditorSupport implements FormAwarePropertyEditor {

    private FormModel formModel;

    private String propertyName;

    private JComponent customEditor;

    protected AbstractCustomPropertyEditor() {
    }

    public void setFormDetails(FormModel formModel, String propertyName) {
        Assert.isTrue(customEditor == null, "setFormDetails must be called before the custom "
                + "editor component is created");
        Assert.notNull(formModel, "formModel must not be null");
        Assert.notNull(propertyName, "propertyName must not be null");
        this.formModel = formModel;
        this.propertyName = propertyName;
    }

    /**
     * Returns the <code>FormModel</code> that contains the property this 
     * editor is responsible for editing.
     */
    protected FormModel getFormModel() {
        return formModel;
    }

    /**
     * Returns the name of the property that this editor is responsible 
     * for editing.
     */
    protected String getPropertyName() {
        return propertyName;
    }

    /**
     * All instances of this class support a custom editor so this method 
     * always returns true.
     */
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * Returns a <code>JComponent</code> that can be used to edit the property bound 
     * to this property editor.
     */
    public Component getCustomEditor() {
        if (customEditor == null) {
            customEditor = createCustomEditor();
            Assert.notNull(customEditor, "Custom editor can not be null");
        }
        return customEditor;
    }

    /**
     * Sets the value of the property to be edited and notifies any subclasses 
     * that the value has changed using the <code>propertyValueChanged</code> 
     * method.
     * 
     * @param newValue 
     *      the new value for the property
     */
    public void setValue(Object newValue) {
        setValueInternal(newValue);
        propertyValueChanged();
    }

    /**
     * Sets the value of the property to be edited but does not notify any subclasses 
     * that the value has changed.
     * <p>
     * This method should be used internally to save any changed that have been made using the custom 
     * editor component. 
     * 
     * @param newValue 
     *      the new value for the property
     */
    protected void setValueInternal(Object propertyValue) {
        super.setValue(propertyValue);
    }

    /**
     * Called whenever the value of the property bound to this property editor 
     * is changed using the <code>setValue</code> method.
     */
    protected abstract void propertyValueChanged();

    /**
     * Return the JComponent which allows the user to edit the 
     * property bound to this property editor.
     */
    protected abstract JComponent createCustomEditor();

    /**
     * This method is not implemented. It will always throw an <code>UnsupportedOperationException</code>.
     */
    public String getAsText() {
        throw new UnsupportedOperationException("This property editor only supports the getValue method.");
    }

    /**
     * This method is not implemented. It will always throw an <code>UnsupportedOperationException</code>.
     */
    public void setAsText(String text) {
        throw new UnsupportedOperationException("This property editor only supports the setValue method.");
    }
}