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
package org.springframework.richclient.command.support;

import java.util.Map;

import org.springframework.richclient.command.GuardedCommandDelegate;
import org.springframework.richclient.command.ParameterizedCommandDelegate;
import org.springframework.rules.values.ValueHolder;
import org.springframework.rules.values.ValueListener;
import org.springframework.rules.values.ValueModel;

/**
 * @author Keith Donald
 */
public class AbstractCommandDelegate implements ParameterizedCommandDelegate,
        GuardedCommandDelegate {

    private ValueModel enabled = new ValueHolder(Boolean.FALSE);

    public boolean isEnabled() {
        return ((Boolean)enabled.get()).booleanValue();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(Boolean.valueOf(enabled));
    }

    public void addEnabledListener(ValueListener listener) {
        enabled.addValueListener(listener);
    }

    public void removeEnabledListener(ValueListener listener) {
        enabled.removeValueListener(listener);
    }

    public void execute(Map parameters) {
        execute();
    }

    public void execute() {
    }

}