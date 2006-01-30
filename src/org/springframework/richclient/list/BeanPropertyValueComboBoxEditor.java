package org.springframework.richclient.list;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.swing.ComboBoxEditor;
import java.awt.event.ActionListener;
import java.awt.Component;

/**
 * @author Geoffrey De Smet
 */
public class BeanPropertyValueComboBoxEditor implements ComboBoxEditor {

    private BeanWrapper beanWrapper = new BeanWrapperImpl();

    private Object current;

    private ComboBoxEditor innerEditor;

    private String renderedProperty;

    /**
     * Constructs a new <code>BeanPropertyValueComboBoxEditor</code>
     * instance. The <code>toString</code> method is used to render
     * the items.
     *
     * @param editor
     *            the <code>ComboBoxEditor</code> to use internally
     */
    public BeanPropertyValueComboBoxEditor(ComboBoxEditor editor) {
        this(editor, null);
    }

    /**
     * Constructs a new <code>BeanPropertyValueComboBoxEditor</code>
     * instance.
     *
     * @param innerEditor
     *            the <code>ComboBoxEditor</code> to use internally
     * @param renderedProperty
     *            the property used to render the items
     */
    public BeanPropertyValueComboBoxEditor(ComboBoxEditor innerEditor, String renderedProperty) {
        this.innerEditor = innerEditor;
        this.renderedProperty = renderedProperty;
    }

    /**
     * Should only be used if the innerEditor will be set later
     *
     * @param renderedProperty
     */
    public BeanPropertyValueComboBoxEditor(String renderedProperty) {
        this(null, renderedProperty);
    }

    public void setInnerEditor(ComboBoxEditor innerEditor) {
        this.innerEditor = innerEditor;
    }

    /**
     * @see javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener)
     */
    public void addActionListener(ActionListener l) {
        innerEditor.addActionListener(l);
    }

    /**
     * @see javax.swing.ComboBoxEditor#getEditorComponent()
     */
    public Component getEditorComponent() {
        return innerEditor.getEditorComponent();
    }

    /**
     * @see javax.swing.ComboBoxEditor#getItem()
     */
    public Object getItem() {
        return current;
    }

    /**
     * @see javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener)
     */
    public void removeActionListener(ActionListener l) {
        innerEditor.removeActionListener(l);
    }

    /**
     * @see javax.swing.ComboBoxEditor#selectAll()
     */
    public void selectAll() {
        innerEditor.selectAll();
    }

    /**
     * @see javax.swing.ComboBoxEditor#setItem(Object)
     */
    public void setItem(Object item) {
        current = item;
        if (item == null) {
            innerEditor.setItem("");
        } else {
            beanWrapper.setWrappedInstance(item);
            if (renderedProperty != null) {
                innerEditor.setItem(String.valueOf(beanWrapper.getPropertyValue(renderedProperty)));
            } else {
                innerEditor.setItem(String.valueOf(item));
            }
        }
    }
}
