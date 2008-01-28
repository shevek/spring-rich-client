/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.text;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.text.JTextComponent;

import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;

/**
 * Abstract base class for <code>FormComponentInterceptor</code>s that work on
 * <code>JTextComponent</code>s.
 * 
 * @author Peter De Bruycker
 * 
 */
public abstract class TextComponentInterceptor extends AbstractFormComponentInterceptor {

    public final void processComponent( String propertyName, JComponent component ) {
        JTextComponent textComponent = getTextComponent( getInnerComponent( component ) );
        if( textComponent != null ) {
            processComponent( propertyName, textComponent );
        }
    }

    /**
     * Process the text component.
     * 
     * @param propertyName the name of the property
     * @param textComponent the text component
     */
    protected abstract void processComponent( String propertyName, JTextComponent textComponent );

    /**
     * Converts the given component to a <code>JTextComponent</code>. This can be a
     * simple cast if the component is already a text component, or an embedded component
     * (for example a JSpinner).
     * <p>
     * This method is protected, and can be overridden when necessary.
     * 
     * @param component the component
     * @return a <code>JTextComponent</code>, or <code>null</code>
     */
    protected JTextComponent getTextComponent( JComponent component ) {
        if( component instanceof JTextField ) {
            return (JTextField) component;
        }

        if( component instanceof JSpinner ) {
            JSpinner spinner = (JSpinner) component;
            if( spinner.getEditor() instanceof JSpinner.DefaultEditor ) {
                return ((DefaultEditor) spinner.getEditor()).getTextField();
            }
            if( spinner.getEditor() instanceof JTextField ) {
                return (JTextField) spinner.getEditor();
            }
        }

        return null;
    }

}
