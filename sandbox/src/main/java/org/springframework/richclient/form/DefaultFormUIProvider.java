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
 *
 */

package org.springframework.richclient.form;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;

/**
 * Default <code>FormUIProvider</code> implementation.
 * 
 * @author Peter De Bruycker
 */
public class DefaultFormUIProvider extends AbstractFormUIProvider {
    private JComponent formComponent;

    public DefaultFormUIProvider() {

    }

    public DefaultFormUIProvider(JComponent component) {
        formComponent = component;
    }

    public JComponent getComponent(String propertyPath) {
        return getComponent(formComponent, propertyPath);
    }

    private static JComponent getComponent(Container parent, String id) {
        Component[] children = parent.getComponents();
        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            if (child instanceof JComponent && id.equals(children[i].getName())) {
                return (JComponent)child;
            }

            if (child instanceof Container) {
                Container container = (Container)child;
                JComponent result = getComponent(container, id);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public void setControl(JComponent control) {
        formComponent = control;
    }

    protected JComponent createControl() {
        return formComponent;
    }
}
