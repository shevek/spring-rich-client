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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.springframework.rules.Algorithms;
import org.springframework.rules.UnaryProcedure;
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;

/**
 * @author Keith Donald
 */
public class GuardedGroup implements Guarded {
    private boolean groupEnabledState;

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

    private static class GuardedValueModel implements Guarded {
        private GuardedGroup guardedGroup;
        
        private ValueModel guardedHolder;

        public GuardedValueModel(GuardedGroup guardedGroup, ValueModel valueModel) {
            this.guardedGroup = guardedGroup;
            this.guardedHolder = valueModel;
            this.guardedHolder.addValueListener(new ValueListener() {
                public void valueChanged() {
                    setEnabled(GuardedValueModel.this.guardedGroup.groupEnabledState);
                }
            });
        }

        public void setEnabled(boolean enabled) {
            Guarded g = (Guarded)guardedHolder.get();
            if (g != null) {
                g.setEnabled(enabled);
            }
        }
    }

    public static final Guarded createGuardedAdapter(final JComponent component) {
        return new Guarded() {
            public void setEnabled(boolean enabled) {
                component.setEnabled(enabled);
            }
        };
    };

    public static final GuardedGroup createGuardedGroup(
            final JComponent[] components) {
        Set guardedSet = new HashSet(components.length);
        for (int i = 0; i < components.length; i++) {
            guardedSet.add(createGuardedAdapter(components[i]));
        }
        GuardedGroup g = new GuardedGroup();
        g.guardedGroup = guardedSet;
        return g;
    };

    public void setEnabled(final boolean enabled) {
        if (this.groupEnabledState == enabled) { return; }
        Algorithms.instance().forEachIn(guardedGroup, new UnaryProcedure() {
            public void run(Object guarded) {
                ((Guarded)guarded).setEnabled(enabled);
            }
        });
        this.groupEnabledState = enabled;
    }

}