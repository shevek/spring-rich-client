package org.springframework.richclient.form.builder.support;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;

import org.springframework.richclient.application.Application;

/**
 * Abstract base class for showing text in the status bar when the form
 * component gains focus.
 * 
 * @author Peter De Bruycker
 */
public abstract class AbstractFormComponentStatusBarInterceptor extends AbstractFormComponentInterceptor {

	protected abstract String getStatusBarText(String propertyName);

	public void processComponent(final String propertyName, final JComponent component) {
		component.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (Application.instance().getActiveWindow() != null) {
					String caption = getStatusBarText(propertyName);
					if (caption != null) {
						Application.instance().getActiveWindow().getStatusBar().setMessage(caption);
					}
				}
			}

			public void focusLost(FocusEvent e) {
				if (Application.instance().getActiveWindow() != null) {
					Application.instance().getActiveWindow().getStatusBar().setMessage("");
				}
			}
		});
	}
}
