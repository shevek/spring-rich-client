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

import java.awt.Frame;
import java.awt.Window;

import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Memento for saving and restoring Window settings.
 * 
 * @author Peter De Bruycker
 */
public class WindowMemento implements Memento {

	private Window window;

	private String key;

	public WindowMemento(Window window) {
		this(window, null);
	}

	public WindowMemento(Window window, String key) {
		Assert.notNull(window, "Window cannot be null");
		Assert.isTrue(StringUtils.hasText(key) || StringUtils.hasText(window.getName()),
				"Key is empty or window has no name");

		if (!StringUtils.hasText(key)) {
			key = window.getName();
		}

		this.window = window;
		this.key = key;
	}

	public void saveState(Settings settings) {
		saveLocation(settings);
		saveSize(settings);
		saveMaximizedState(settings);
	}

	void saveMaximizedState(Settings settings) {
		if (window instanceof Frame) {
			Frame frame = (Frame) window;
			settings.setBoolean(key + ".maximized", frame.getExtendedState() == Frame.MAXIMIZED_BOTH);
		}
	}

	void saveSize(Settings settings) {
		settings.setInt(key + ".height", window.getHeight());
		settings.setInt(key + ".width", window.getWidth());
	}

	void saveLocation(Settings settings) {
		settings.setInt(key + ".x", window.getX());
		settings.setInt(key + ".y", window.getY());
	}

	public void restoreState(Settings settings) {
		restoreLocation(settings);
		restoreSize(settings);
		restoreMaximizedState(settings);
	}

	void restoreMaximizedState(Settings settings) {
		if (window instanceof Frame) {
			Frame frame = (Frame) window;
			frame.setExtendedState((settings.getBoolean(key + ".maximized") ? Frame.MAXIMIZED_BOTH : Frame.NORMAL));
		}
	}

	void restoreSize(Settings settings) {
		if (settings.contains(key + ".height") && settings.contains(key + ".width")) {
			window.setSize(settings.getInt(key + ".width"), settings.getInt(key + ".height"));
		}
	}

	void restoreLocation(Settings settings) {
		if (settings.contains(key + ".x") && settings.contains(key + ".y")) {
			window.setLocation(settings.getInt(key + ".x"), settings.getInt(key + ".y"));
		}
	}

	public Window getWindow() {
		return window;
	}

	public String getKey() {
		return key;
	}
}
