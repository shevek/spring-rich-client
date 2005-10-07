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
package org.springframework.richclient.settings.xml;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsException;
import org.springframework.richclient.settings.SettingsFactory;

/**
 * @author Peter De Bruycker
 */
public class XmlSettingsFactory implements SettingsFactory {
	private String location;

	public Settings createSettings(String key) throws SettingsException {
		try {
			return new XmlSettings();
		} catch (ParserConfigurationException e) {
			throw new SettingsException("Unable to create xmlsettings with key " + key, e);
		} catch (FactoryConfigurationError e) {
			throw new SettingsException("Unable to create xmlsettings with key " + key, e);
		}
	}

	/**
	 * Returns the location for the xml files.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location of the xml files.
	 * 
	 * @param location
	 *            the location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
