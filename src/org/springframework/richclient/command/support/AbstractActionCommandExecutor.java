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

import org.springframework.binding.value.ValueChangeListener;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.command.GuardedActionCommandExecutor;
import org.springframework.richclient.command.ParameterizableActionCommandExecutor;

/**
 * @author Keith Donald
 */
public class AbstractActionCommandExecutor implements ParameterizableActionCommandExecutor,
		GuardedActionCommandExecutor {

	private ValueModel enabled = new ValueHolder(Boolean.FALSE);

	public boolean isEnabled() {
		return ((Boolean)enabled.getValue()).booleanValue();
	}

	public void setEnabled(boolean enabled) {
		this.enabled.setValue(Boolean.valueOf(enabled));
	}

	public void addEnabledListener(ValueChangeListener listener) {
		enabled.addValueChangeListener(listener);
	}

	public void removeEnabledListener(ValueChangeListener listener) {
		enabled.removeValueChangeListener(listener);
	}

	public void execute(Map parameters) {
		execute();
	}

	public void execute() {
	}

}