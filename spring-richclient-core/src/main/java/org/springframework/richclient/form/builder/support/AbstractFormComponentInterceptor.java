/*
 * Copyright 2004 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.richclient.form.builder.support;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.util.Assert;

/**
 * Abstract base for
 * {@link org.springframework.richclient.form.builder.FormComponentInterceptorFactory}
 * with formModel handling.
 * 
 * @author oliverh
 */
public abstract class AbstractFormComponentInterceptor implements FormComponentInterceptor {

	private final FormModel formModel;

	protected AbstractFormComponentInterceptor() {
		formModel = null;
	}

	protected AbstractFormComponentInterceptor(FormModel formModel) {
		Assert.notNull(formModel);
		this.formModel = formModel;
	}

	protected FormModel getFormModel() {
		return formModel;
	}

	public void processLabel(String propertyName, JComponent label) {
	}

	public void processComponent(String propertyName, JComponent component) {
	}

	/**
	 * Check for JScrollPane.
	 * 
	 * @param component
	 * @return the component itself, or the inner component if it was a
	 * JScrollPane.
	 */
	protected JComponent getInnerComponent(JComponent component) {
		if (component instanceof JScrollPane) {
			return (JComponent) ((JScrollPane) component).getViewport().getView();
		}
		return component;
	}
}