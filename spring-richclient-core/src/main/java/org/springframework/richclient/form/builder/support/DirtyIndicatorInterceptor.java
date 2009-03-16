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
package org.springframework.richclient.form.builder.support;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueChangeDetector;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.util.OverlayHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * Adds a "dirty overlay" to a component that is triggered by user editing. The
 * overlaid image is retrieved by the image key "dirty.overlay". The image is
 * placed at the top-left corner of the component, and the image's tooltip is
 * set to a message (retrieved with key "dirty.message") such as "{field} has
 * changed, original value was {value}.". It also adds a small revert button
 * that resets the value of the field.
 * 
 * @author Peter De Bruycker
 */
public class DirtyIndicatorInterceptor extends AbstractFormComponentInterceptor {
	private static final String DIRTY_ICON_KEY = "dirty.overlay";

	private static final String DIRTY_MESSAGE_KEY = "dirty.message";

	private static final String REVERT_ICON_KEY = "revert.overlay";

	private static final String REVERT_MESSAGE_KEY = "revert.message";

	private ValueChangeDetector valueChangeDetector;

	public DirtyIndicatorInterceptor(FormModel formModel) {
		super(formModel);
	}

	public void processComponent(final String propertyName, final JComponent component) {
		final OriginalValueHolder originalValueHolder = new OriginalValueHolder();
		final DirtyOverlay overlay = new DirtyOverlay(getFormModel(), propertyName, originalValueHolder);

		final ValueHolder reset = new ValueHolder(Boolean.FALSE);
		getFormModel().getValueModel(propertyName).addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (reset.getValue() == Boolean.TRUE) {
					originalValueHolder.reset();
					reset.setValue(Boolean.FALSE);

					overlay.setVisible(false);

					return;
				}

				if (!originalValueHolder.isInitialized()) {
					originalValueHolder.setOriginalValue(evt.getOldValue());
				}

				Object oldValue = originalValueHolder.getValue();
				Object newValue = evt.getNewValue();
				overlay.setVisible(getValueChangeDetector().hasValueChanged(oldValue, newValue)
                    && !getFormModel().getFieldMetadata(propertyName).isReadOnly());
			}
		});
		getFormModel().getFormObjectHolder().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				// reset original value, new "original" value is in the form
				// model as the form object has changed
				reset.setValue(Boolean.TRUE);
			}
		});

		InterceptorOverlayHelper.attachOverlay(overlay.getControl(), component, OverlayHelper.NORTH_WEST, 5, 0);
		overlay.setVisible(false);
	}

	private ValueChangeDetector getValueChangeDetector() {
		if (valueChangeDetector == null) {
			valueChangeDetector = (ValueChangeDetector) ApplicationServicesLocator.services().getService(
					ValueChangeDetector.class);
		}

		return valueChangeDetector;
	}

	private static class DirtyOverlay extends AbstractControlFactory {
		private JButton revertButton;

		private JLabel dirtyLabel;

		private FormModel formModel;

		private String propertyName;

		private OriginalValueHolder originalValueHolder;

		public DirtyOverlay(FormModel formModel, String propertyName, OriginalValueHolder originalValueHolder) {
			this.formModel = formModel;
			this.propertyName = propertyName;
			this.originalValueHolder = originalValueHolder;
		}

		protected JComponent createControl() {
			final JPanel control = new JPanel(new BorderLayout()) {
				public void repaint() {
					// hack for RCP-426: if the form component is on a tabbed
					// pane, when switching between tabs when the overlay is
					// visible, the overlay is not correctly repainted. When we
					// trigger a revalidate here, everything is ok.
					revalidate();

					super.repaint();
				}
			};

			control.setName("dirtyOverlay");

			control.setOpaque(true);

			IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
			Icon icon = iconSource.getIcon(DIRTY_ICON_KEY);
			dirtyLabel = new JLabel(icon);
			control.add(dirtyLabel, BorderLayout.CENTER);

			createRevertButton();
			control.add(revertButton, BorderLayout.LINE_END);

			return control;
		}

		private void createRevertButton() {
			IconSource iconSource = (IconSource) ApplicationServicesLocator.services().getService(IconSource.class);
			Icon icon = iconSource.getIcon(REVERT_ICON_KEY);

			revertButton = new JButton(icon);
			revertButton.setBorderPainted(false);
			revertButton.setContentAreaFilled(false);
			revertButton.setFocusable(false);
			revertButton.setMargin(new Insets(-3, -3, -3, -3));
			revertButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// reset
					formModel.getValueModel(propertyName).setValue(originalValueHolder.getValue());
				}
			});
		}

		public void setVisible(boolean visible) {
			getControl().setVisible(visible);
			// manually set the size, otherwise sometimes the overlay is not
			// shown (it has size 0,0)
			getControl().setSize(getControl().getPreferredSize());

			if (visible) {
				MessageSource messageSource = (MessageSource) ApplicationServicesLocator.services().getService(
						MessageSource.class);
				String dirtyTooltip = messageSource.getMessage(DIRTY_MESSAGE_KEY, new Object[] {
						formModel.getFieldFace(propertyName).getDisplayName(), originalValueHolder.getValue() }, Locale
						.getDefault());
				dirtyLabel.setToolTipText(dirtyTooltip);

				String revertTooltip = messageSource.getMessage(REVERT_MESSAGE_KEY, new Object[] { formModel
						.getFieldFace(propertyName).getDisplayName() }, Locale.getDefault());
				revertButton.setToolTipText(revertTooltip);
			}
		}
	}

	private static class OriginalValueHolder {
		private boolean initialized;

		private Object originalValue;

		public void setOriginalValue(Object value) {
			initialized = true;
			originalValue = value;
		}

		public void reset() {
			initialized = false;
			originalValue = null;
		}

		public Object getValue() {
			return originalValue;
		}

		public boolean isInitialized() {
			return initialized;
		}
	}
}
