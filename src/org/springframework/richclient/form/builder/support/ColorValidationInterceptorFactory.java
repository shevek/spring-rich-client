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
package org.springframework.richclient.form.builder.support;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.util.Assert;

/**
 * Adds an "overlay" to a component that is triggered by a validation event
 * for JTextComponents.  When an error is triggered, the background color
 * of the component is changed to the color set in
 * {@link #setErrorColor(Color)}.  (The default color is a very light red.)
 *
 * @author oliverh
 */
public class ColorValidationInterceptorFactory implements
    FormComponentInterceptorFactory {

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

    private class ColorValidationInterceptor extends
        ValidationInterceptor {

        public ColorValidationInterceptor(FormModel formModel) {
            super(formModel);
        }

        public JComponent processComponent(String propertyName,
                                           JComponent component) {
            JComponent innerComp = getInnerComponent(component);
            if (innerComp instanceof JTextComponent) {
                ColorChanger colorChanger = new ColorChanger(component);
                registerErrorGuarded(propertyName, colorChanger);
            }
            return component;
        }
    }

    private class ColorChanger implements Guarded {
        private Color normalColor;

        private JComponent component;

        public ColorChanger(JComponent component) {
            this.component = component;
            this.normalColor = component.getBackground();
        }

        public boolean isEnabled() {
            return false;
        }

        public void setEnabled(boolean enabled) {
            component.setBackground(enabled ? normalColor : errorColor);
        }
    }
}

