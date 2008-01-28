package org.springframework.richclient.list;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.ComboBoxEditor;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.enums.LabeledEnum;
import org.springframework.util.Assert;

/**
 * <code>ComboBoxEditor</code> that wraps another editor, but performs
 * conversion between <code>CodedEnum</code> s and <code>String</code>s.
 * <br/>It wraps another <code>ComboBoxEditor</code> to avoid visual
 * differences between the default editor and this editor.
 * 
 * @author peter.de.bruycker
 */
public class LabeledEnumComboBoxEditor implements ComboBoxEditor {

	private Object current;

	private MessageSource messages;

	private ComboBoxEditor inner;

	/**
	 * Constructs a new <code>CodedEnumComboBoxEditor</code> instance.
	 * 
	 * @param messageSource the <code>MessageSource</code> to use for
	 *        conversion
	 * @param editor the <code>ComboBoxEditor</code> to use internally
	 */
	public LabeledEnumComboBoxEditor(MessageSource messageSource, ComboBoxEditor editor) {
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
		if (anObject != null) {
			if (messages != null && anObject instanceof MessageSourceResolvable) {
				inner.setItem(messages.getMessage((MessageSourceResolvable)anObject, Locale.getDefault()));
			}
			else {
				inner.setItem(((LabeledEnum)anObject).getLabel());
			}
		}
		else {
			inner.setItem(null);
		}
	}
}