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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import org.springframework.rules.Algorithms;
import org.springframework.rules.UnaryProcedure;

/**
 * @author Keith Donald
 */
public class GuardedGroup implements Guarded {

    private List guardedGroup;

    private GuardedGroup() {

    }

    public GuardedGroup(Guarded[] guarded) {
        this.guardedGroup = Arrays.asList(guarded);
    }

    public void addGuarded(Guarded guarded) {
        this.guardedGroup.add(guarded);
    }

    public static final Guarded createGuardedAdapter(final JComponent component) {
        return new Guarded() {
            public void setEnabled(boolean enabled) {
                component.setEnabled(enabled);
            }
        };
    };

    public static final GuardedGroup createGuardedGroup(final JComponent[] components) {
        List guardedList = new ArrayList(components.length);
        for (int i = 0; i < components.length; i++) {
            guardedList.add(createGuardedAdapter(components[i]));
        }
        GuardedGroup g = new GuardedGroup();
        g.guardedGroup = guardedList;
        return g;
    };

    /*
     * @see org.springframework.rcp.core.Guarded#setEnabled(boolean)
     */
    public void setEnabled(final boolean enabled) {
        Algorithms.instance().forEachIn(guardedGroup, new UnaryProcedure() {
            public void run(Object guarded) {
                ((Guarded)guarded).setEnabled(enabled);
            }
        });
    }

}