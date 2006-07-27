/*
 * Copyright 2002-2006 the original author or authors.
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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.richclient.form.builder.support.AbstractFormComponentInterceptor;

/**
 * Implements "select all" behaviour for form components. If the form component is a text field, or a spinner, the
 * contents of the component are selected if it receives focus.
 * 
 * @author Peter De Bruycker
 */
public class SelectAllFormComponentInterceptorFactory implements FormComponentInterceptorFactory {

    public FormComponentInterceptor getInterceptor( FormModel formModel ) {
        return new SelectAllFormComponentInterceptor();
    }

    public class SelectAllFormComponentInterceptor extends AbstractFormComponentInterceptor {
        private FocusListener selector = new FocusAdapter() {

            public void focusGained( FocusEvent e ) {
                if( !e.isTemporary() ) {
                    final JTextComponent textComponent = (JTextComponent) e.getComponent();
                    // using invokeLater as fix for bug 4740914
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            textComponent.selectAll();
                        }
                    } );
                }
            }
        };

        public void processComponent( String propertyName, JComponent component ) {
            JTextComponent textComponent = getTextComponent( getInnerComponent( component ) );
            if( textComponent != null ) {
                textComponent.addFocusListener( selector );
            }
        }

        private JTextComponent getTextComponent( JComponent component ) {
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

}
