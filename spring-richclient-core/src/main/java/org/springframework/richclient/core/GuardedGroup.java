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
package org.springframework.richclient.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.springframework.binding.value.ValueModel;
import org.springframework.rules.closure.support.Algorithms;
import org.springframework.rules.closure.support.Block;

/**
 * @author Keith Donald
 */
public class GuardedGroup implements Guarded {
    private Boolean groupEnabledState;

    private Set guardedGroup;

    private GuardedGroup() {

    }

    public GuardedGroup(Guarded[] guarded) {
        this.guardedGroup = new HashSet(Arrays.asList(guarded));
    }

    public void addGuarded(Guarded guarded) {
        this.guardedGroup.add(guarded);
    }

    public void addGuardedHolder(ValueModel guardedHolder) {
        this.guardedGroup.add(new GuardedValueModel(this, guardedHolder));
    }

    public boolean isEnabled() {
        if (groupEnabledState == null) {
            return false;
        }
        return groupEnabledState.booleanValue();
    }

    public void setEnabled(final boolean enabled) {
        if (this.groupEnabledState != null && this.groupEnabledState.booleanValue() == enabled) {
            return;
        }
        Algorithms.instance().forEach(guardedGroup, new Block() {
            protected void handle(Object guarded) {
                ((Guarded)guarded).setEnabled(enabled);
            }
        });
        this.groupEnabledState = Boolean.valueOf(enabled);
    }

    private static class GuardedValueModel implements Guarded, PropertyChangeListener {
        private GuardedGroup guardedGroup;

        private ValueModel guardedHolder;

        public GuardedValueModel(GuardedGroup guardedGroup, ValueModel valueModel) {
            this.guardedGroup = guardedGroup;
            this.guardedHolder = valueModel;
            this.guardedHolder.addValueChangeListener(this);
        }

        public boolean isEnabled() {
            Guarded g = (Guarded)guardedHolder.getValue();
            if (g != null)
                return g.isEnabled();

            return false;
        }

        public void setEnabled(boolean enabled) {
            Guarded g = (Guarded)guardedHolder.getValue();
            if (g != null) {
                g.setEnabled(enabled);
            }
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            Boolean groupEnabled = GuardedValueModel.this.guardedGroup.groupEnabledState;
            if (groupEnabled != null) {
                setEnabled(groupEnabled.booleanValue());
            }
        }
    }

    public static final Guarded createGuardedAdapter(final JComponent component) {
        if (component instanceof JTextComponent) {
            // JTextComponents are different from most JComponents in that
            // they are best disabled by invoking the setEditable method.
            // setEnabled(false) completely disables the control -- including
            // the ability to copy to the clipboard.
            final JTextComponent textComp = (JTextComponent)component;
            return new Guarded() {
                public boolean isEnabled() {
                    return textComp.isEditable();
                }

                public void setEnabled(boolean enabled) {
                    textComp.setEditable(enabled);
                }
            };
        }

        return new Guarded() {
            public boolean isEnabled() {
                return component.isEnabled();
            }

            public void setEnabled(boolean enabled) {
                component.setEnabled(enabled);
            }
        };
    }

    private static final Guarded createGuardedAdapter(final Object component) {
        if (component.getClass().isArray())
            return doCreateGuardedGroup((Object[])component);

        return createGuardedAdapter((JComponent)component);
    }

    public static final GuardedGroup createGuardedGroup(Object[] components) {
        return doCreateGuardedGroup(components);
    }

    public static final GuardedGroup createGuardedGroup(final JComponent[] components) {
        return doCreateGuardedGroup(components);
    }

    public static final GuardedGroup createGuardedGroup(JComponent[][] componentArrays) {
        return doCreateGuardedGroup(componentArrays);
    }

    private static GuardedGroup doCreateGuardedGroup(Object[] components) {
        Set guardedSet = new HashSet(components.length);
        for (int i = 0; i < components.length; i++) {
            guardedSet.add(createGuardedAdapter(components[i]));
        }
        GuardedGroup g = new GuardedGroup();
        g.guardedGroup = guardedSet;
        return g;
    }

}