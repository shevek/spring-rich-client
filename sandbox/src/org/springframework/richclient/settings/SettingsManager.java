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
package org.springframework.richclient.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Facade for working with <code>Settings</code>. Provides methods for
 * <code>Settings</code> creation, export, import, ...
 * <p>
 * Creation of <code>Settings</code> is delegated to a
 * <code>SettingsFactory</code>.
 * <p>
 * The export and import of <code>Settings</code> is done with xml files
 * 
 * @author Peter De Bruycker
 */
public class SettingsManager {
	private SettingsFactory settingsFactory = new TransientSettingsFactory();

	private Settings internalSettings;

	private Settings userSettings;

	public static final String INTERNAL = "internal";

	public static final String USER = "user";

	/**
	 * Returns the internal settings, i.e. the settings used for storing ui
	 * state, and other settings normally not visible and configurable to the
	 * user.
	 * 
	 * @return the internal <code>Settings</code>
	 * @throws SettingsException
	 */
	public Settings getInternalSettings() throws SettingsException {
		if (internalSettings == null) {
			internalSettings = createSettings(INTERNAL);
		}
		return internalSettings;
	}

	/**
	 * Returns the user settings, i.e. the settings used for the user
	 * preferences, normally these settings can be changed by the user, and
	 * affect the applications appearance and behaviour. These settings can also
	 * be exported/imported.
	 * 
	 * @return the user <code>Settings</code>
	 * @throws SettingsException
	 */
	public Settings getUserSettings() throws SettingsException {
		if (userSettings == null) {
			userSettings = createSettings(USER);
		}
		return userSettings;
	}

	/**
	 * Returns the <code>Settings</code> for the given key. This method should
	 * not be called directly, use <code>{@link #getInternalSettings()}</code>
	 * or <code>{@link #getUserSettings()}</code> instead.
	 * 
	 * @param key
	 *            the key
	 * @return the Settings
	 * @throws SettingsException
	 *             if the <code>Settings</code> could not be created
	 */
	public Settings createSettings(String key) throws SettingsException {
		return settingsFactory.createSettings(key);
	}

	/**
	 * Set the settings factory. If the factory is set to <code>null</code>,
	 * the TransientSettingsFactory will be used.
	 * 
	 * @param factory
	 *            the factory
	 */
	public void setSettingsFactory(SettingsFactory factory) {
		settingsFactory = factory;
		if (settingsFactory == null) {
			settingsFactory = new TransientSettingsFactory();
		}
	}

	/**
	 * Returns the settings factory.
	 * 
	 * @return the factory
	 */
	public SettingsFactory getSettingsFactory() {
		return settingsFactory;
	}

	/**
	 * Export <code>settings</code> to an <code>OutputStream</code>
	 * 
	 * @param settings
	 *            the <code>settings</code>
	 * @param out
	 *            the <code>OutputStream</code>
	 * @throws IOException
	 *             if the settings could not be exported
	 */
	public void exportSettings(Settings settings, OutputStream out) throws IOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Import <code>settings</code> from an <code>InputStream</code>
	 * 
	 * @param settings
	 *            the <code>settings</code>
	 * @param in
	 *            the <code>InputStream</code>
	 * @throws IOException
	 *             if the settings could not be imported
	 */
	public void importSettings(Settings settings, InputStream in) throws IOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
