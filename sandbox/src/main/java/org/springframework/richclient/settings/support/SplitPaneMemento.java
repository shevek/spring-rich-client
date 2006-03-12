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
package org.springframework.richclient.settings.support;

import javax.swing.JSplitPane;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Peter De Bruycker
 */
public class SplitPaneMemento implements Memento {

	private JSplitPane splitPane;

	private String key;

	public SplitPaneMemento(JSplitPane splitPane) {
		this(splitPane, null);
	}

	public SplitPaneMemento(JSplitPane splitPane, String key) {
		Assert.notNull(splitPane, "SplitPane cannot be null");
		Assert.isTrue(StringUtils.hasText(key) || StringUtils.hasText(splitPane.getName()),
				"Key is empty or splitpane has no name");

		if (!StringUtils.hasText(key)) {
			key = splitPane.getName();
		}

		this.splitPane = splitPane;
		this.key = key;
	}

	public void saveState(Settings settings) {
		settings.setInt(key + ".dividerLocation", splitPane.getDividerLocation());
	}

	public void restoreState(Settings settings) {
		if (settings.contains(key + ".dividerLocation")) {
			splitPane.setDividerLocation(settings.getInt(key + ".dividerLocation"));
		}
	}

	public JSplitPane getSplitPane() {
		return splitPane;
	}

	public String getKey() {
		return key;
	}
}
