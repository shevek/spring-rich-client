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
package org.springframework.richclient.form.builder.support;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.util.Assert;

/**
 * Adds an "overlay" to a component that is triggered by a validation event for
 * JTextComponents. When an error is triggered, the background color of the
 * component is changed to the color set in {@link #setErrorColor(Color)}. (The
 * default color is a very light red.)
 * 
 * @author oliverh
 */
public class ColorValidationInterceptorFactory implements FormComponentInterceptorFactory {

	private static final Color DEFAULT_ERROR_COLOR = new Color(255, 240, 240);

	private Color errorColor = DEFAULT_ERROR_COLOR;

	public ColorValidationInterceptorFactory() {
	}

	public void setErrorColor(Color errorColor) {
		Assert.notNull(errorColor);
		this.errorColor = errorColor;
	}

	public FormComponentInterceptor getInterceptor(FormModel formModel) {
		return new ColorValidationInterceptor(formModel);
	}

	private class ColorValidationInterceptor extends ValidationInterceptor {

		public ColorValidationInterceptor(FormModel formModel) {
			super(formModel);
		}

		public void processComponent(String propertyName, JComponent component) {
			JComponent innerComponent = getInnerComponent(component);
			if (innerComponent instanceof JTextComponent) {
				ColorChanger colorChanger = new ColorChanger(innerComponent, errorColor);
				registerGuarded(propertyName, colorChanger);
			}
		}
	}

	static class ColorChanger implements Guarded {
		private Color normalColor;

		private JComponent component;

		private boolean changingColor;

		private Color errorColor;

		private boolean enabled;

		public ColorChanger(JComponent component, Color errorColor) {
			this.normalColor = component.getBackground();
			this.errorColor = errorColor;
			this.component = component;

			component.addPropertyChangeListener("background", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if (changingColor) {
						return;
					}

					normalColor = (Color) evt.getNewValue();

					// this call is needed so if the color is set when the
					// errorColor is showing
					// the errorColor will still be shown afterward
					setEnabled(enabled);
				}
			});
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			try {
				changingColor = true;
				this.enabled = enabled;
				component.setBackground(enabled ? normalColor : errorColor);
			}
			finally {
				changingColor = false;
			}
		}
	}
}
