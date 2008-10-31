/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.factory;

import javax.swing.AbstractButton;

/**
 * Marker for button factories. All methods return {@link AbstractButton}s so
 * that you can provide a custom implementation.
 *
 * @author Keith Donald
 */
public interface ButtonFactory {

	/**
	 * Returns a standard button.
	 */
	public AbstractButton createButton();

	/**
	 * Returns a checkBox.
	 */
	public AbstractButton createCheckBox();

	/**
	 * Returns a toggleButton.
	 */
	public AbstractButton createToggleButton();

	/**
	 * Returns a radioButton.
	 */
	public AbstractButton createRadioButton();
}