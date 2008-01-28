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

import javax.swing.JTree;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Peter De Bruycker
 */
public class TreeMemento implements Memento {

	private static final String EXPANSION_STATE = "expansionState";

	private static final String SELECTED_ROWS = "selectedRows";

	private String key;

	private JTree tree;

	public TreeMemento(JTree tree) {
		this(tree, null);
	}

	public TreeMemento(JTree tree, String key) {
		Assert.notNull(tree, "tree cannot be null");
		Assert.isTrue(StringUtils.hasText(key) || StringUtils.hasText(tree.getName()),
				"Key is empty or tree has no name");

		if (!StringUtils.hasText(key)) {
			key = tree.getName();
		}

		this.tree = tree;
		this.key = key;
	}

	public JTree getTree() {
		return tree;
	}

	public String getKey() {
		return key;
	}

	public void restoreState(Settings settings) {
		restoreExpansionState(settings);
		restoreSelectionState(settings);
	}

	public void saveState(Settings settings) {
		saveExpansionState(settings);
		saveSelectionState(settings);
	}

	void saveExpansionState(Settings settings) {
		int rowCount = tree.getRowCount();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rowCount; i++) {
			sb.append(tree.isExpanded(i) ? 1 : 0);
			if (i < rowCount - 1) {
				sb.append(",");
			}
		}
		settings.setString(key + "." + EXPANSION_STATE, sb.toString());
	}

	void restoreExpansionState(Settings settings) {
		String expansionKey = key + "." + EXPANSION_STATE;
		if (settings.contains(expansionKey)) {
			String[] states = settings.getString(expansionKey).split(",");

			try {
				int[] expansionStates = ArrayUtil.toIntArray(states);

				for (int i = 0; i < expansionStates.length; i++) {
					if (expansionStates[i] == 1) {
						tree.expandRow(i);
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO log this
			}
		}
	}

	/**
	 * TODO store lead and anchor selection
	 */
	void saveSelectionState(Settings settings) {
		String selectionKey = key + "." + SELECTED_ROWS;
		if (settings.contains(selectionKey)) {
			settings.remove(selectionKey);
		}

		if (tree.getSelectionCount() > 0) {
			String selectionString = ArrayUtil.asIntervalString(tree.getSelectionRows());
			if (selectionString.length() > 0) {
				settings.setString(selectionKey, selectionString);
			}
		}
	}

	void restoreSelectionState(Settings settings) {
		tree.getSelectionModel().clearSelection();

		String selectionKey = key + "." + SELECTED_ROWS;
		if (settings.contains(selectionKey)) {
			String selection = settings.getString(selectionKey);
			if (StringUtils.hasText(selection)) {
				String[] parts = selection.split(",");
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].indexOf('-') >= 0) {
						String[] tmp = parts[i].split("-");
						tree.addSelectionInterval(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
					} else {
						int index = Integer.parseInt(parts[i]);
						tree.addSelectionRow(index);
					}
				}
			}
		}
	}
}