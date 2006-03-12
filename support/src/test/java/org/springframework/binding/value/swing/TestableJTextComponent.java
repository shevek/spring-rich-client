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
package org.springframework.binding.value.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * A JFormattedTextField that has methods to simulate user interaction
 * 
 * @author Oliver Hutchison
 */
public class TestableJTextComponent extends JFormattedTextField {

    private List focusListeners = new ArrayList();

    /**
     * Simulates gaining focus.
     */
    public void gainFocus() {
        FocusEvent focusEvent = new FocusEvent(this, FocusEvent.FOCUS_GAINED);
        for (Iterator i = focusListeners.iterator(); i.hasNext();) {
            ((FocusListener)i.next()).focusGained(focusEvent);
        }
    }

    /**
     * Simulates losing focus.
     */
    public void loseFocus() {
        FocusEvent focusEvent = new FocusEvent(this, FocusEvent.FOCUS_LOST);
        for (Iterator i = focusListeners.iterator(); i.hasNext();) {
            ((FocusListener)i.next()).focusLost(focusEvent);
        }
    }

    /**
     * Simulates text being typed.
     */
    public void typeText(String text) {
        Document doc = getDocument();
        for (int i = 0; i < text.length(); i++) {
            try {
                doc.insertString(getCaretPosition(), new String(text.substring(i, i + 1)), null);
                setCaretPosition(getCaretPosition() + 1);
            }
            catch (BadLocationException e) {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    /**
     * Simulates backspace being pressed.
     */
    public void typeBackSpace() {
        Document doc = getDocument();
        try {
            doc.remove(getCaretPosition() - 1, 1);
            setCaretPosition(getCaretPosition() - 1);
        }
        catch (BadLocationException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public void addFocusListener(FocusListener listener) {
        super.addFocusListener(listener);
        if (focusListeners != null) {
            focusListeners.add(listener);
        }
    }
}