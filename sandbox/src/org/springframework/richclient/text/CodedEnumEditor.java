package org.springframework.richclient.text;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.ComboBoxEditor;

import org.springframework.context.MessageSource;
import org.springframework.enums.CodedEnum;
import org.springframework.util.Assert;

/**
 * @author peter.de.bruycker
 */
public class CodedEnumEditor implements ComboBoxEditor {

    private Object current;

    private MessageSource messages;

    private ComboBoxEditor inner;

    public CodedEnumEditor(MessageSource messageSource, ComboBoxEditor editor) {
        Assert.notNull(editor, "Editor cannot be null");
        this.inner = editor;
        messages = messageSource;
    }

    /** 
     * @see javax.swing.ComboBoxEditor#selectAll()
     */
    public void selectAll() {
        inner.selectAll();
    }

    /** 
     * @see javax.swing.ComboBoxEditor#getEditorComponent()
     */
    public Component getEditorComponent() {
        return inner.getEditorComponent();
    }

    /** 
     * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        inner.addActionListener(l);
    }

    /** 
     * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
     */
    public void removeActionListener(ActionListener l) {
        inner.removeActionListener(l);
    }

    /** 
     * @see javax.swing.ComboBoxEditor#getItem()
     */
    public Object getItem() {
        return current;
    }

    /** 
     * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
     */
    public void setItem(Object anObject) {
        current = anObject;
        if (messages != null) {
            inner.setItem(messages.getMessage((CodedEnum) anObject, Locale.getDefault()));
        }
        else {
            inner.setItem(((CodedEnum) anObject).getLabel());
        }
    }
}
