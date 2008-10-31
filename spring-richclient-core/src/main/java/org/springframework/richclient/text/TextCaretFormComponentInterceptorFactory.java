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

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;

/**
 * If the text is set in a text component, the caret position is set to the end of the
 * text.
 * <p>
 * This means the beginning of the text will not be visible if the text is too long to fit
 * in the text component.
 * <p>
 * This <code>FormComponentInterceptor</code> "fixes" this behaviour by listening to
 * <code>Document</code> updates, and setting the caret position to 0 (i.e. the
 * beginning of the text) if the text is updated when the text component doesn't have the
 * focus (i.e. the text is not changed by the user).
 * 
 * @author Peter De Bruycker
 */
public class TextCaretFormComponentInterceptorFactory implements FormComponentInterceptorFactory {

    /**
     * Create a new <code>TextCaretFixerComponentInterceptor</code> instance
     * 
     * @return the interceptor
     */
    public FormComponentInterceptor getInterceptor( FormModel formModel ) {
        return new TextCaretComponentInterceptor();
    }

    /**
     * The <code>FormComponentInterceptor</code> implementation.
     */
    public class TextCaretComponentInterceptor extends TextComponentInterceptor {
        protected void processComponent( String propertyName, final JTextComponent textComponent ) {
            textComponent.getDocument().addDocumentListener( new DocumentHandler( textComponent ) );
        }
    }

    private static final class DocumentHandler implements DocumentListener {
        private JTextComponent component;

        private DocumentHandler( JTextComponent component ) {
            this.component = component;
        }

        public void removeUpdate( DocumentEvent e ) {
            fixCaret();
        }

        public void insertUpdate( DocumentEvent e ) {
            fixCaret();
        }

        public void changedUpdate( DocumentEvent e ) {
            fixCaret();
        }

        private void fixCaret() {
            if( !component.hasFocus() ) {
                // need to invoke later, as the text change also changes the caret
                // position
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        component.setCaretPosition( 0 );
                    }
                } );
            }
        }
    }
}
