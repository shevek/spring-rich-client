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

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class SettingsManagerTests extends TestCase {
	public void testSetSettingsFactory() {
		SettingsManager settingsManager = new SettingsManager();
		assertTrue("Default must be TransientSettingsFactory",
				settingsManager.getSettingsFactory() instanceof TransientSettingsFactory);

		TestableSettingsFactory factory = new TestableSettingsFactory();
		settingsManager.setSettingsFactory(factory);
		assertEquals(factory, settingsManager.getSettingsFactory());

		settingsManager.setSettingsFactory(null);
		assertTrue("null resets factory", settingsManager.getSettingsFactory() instanceof TransientSettingsFactory);
	}

	public void testGetInternalSettings() throws SettingsException {
		SettingsManager settingsManager = new SettingsManager();

		TestableSettingsFactory factory = new TestableSettingsFactory();
		settingsManager.setSettingsFactory(factory);

		TransientSettings settings = new TransientSettings();
		factory.setSettings(settings);

		Settings internalSettings = settingsManager.getInternalSettings();
		assertSame(settings, internalSettings);
		assertEquals(1, factory.getCount());
		assertEquals("internal", factory.getKey());

		factory.reset();

		Settings internalSettings2 = settingsManager.getInternalSettings();
		assertSame(internalSettings, internalSettings2);
		assertEquals(0, factory.getCount());
	}

	public void testGetUserSettings() throws SettingsException {
		SettingsManager settingsManager = new SettingsManager();

		TestableSettingsFactory factory = new TestableSettingsFactory();
		settingsManager.setSettingsFactory(factory);

		TransientSettings settings = new TransientSettings();
		factory.setSettings(settings);

		Settings userSettings = settingsManager.getUserSettings();
		assertSame(settings, userSettings);
		assertEquals(1, factory.getCount());
		assertEquals("user", factory.getKey());

		factory.reset();

		Settings userSettings2 = settingsManager.getUserSettings();
		assertSame(userSettings, userSettings2);
		assertEquals(0, factory.getCount());
	}

	public void testCreateSettings() throws SettingsException {
		SettingsManager settingsManager = new SettingsManager();

		TestableSettingsFactory factory = new TestableSettingsFactory();
		settingsManager.setSettingsFactory(factory);

		TransientSettings settings = new TransientSettings();
		factory.setSettings(settings);

		Settings otherSettings = settingsManager.createSettings("key");
		assertSame(settings, otherSettings);
		assertEquals(1, factory.getCount());
		assertEquals("key", factory.getKey());

		factory.reset();
		factory.setSettings(settings);

		Settings otherSettings2 = settingsManager.createSettings("key");
		assertSame(settings, otherSettings2);
		assertEquals(1, factory.getCount());
		assertEquals("key", factory.getKey());
	}

}
