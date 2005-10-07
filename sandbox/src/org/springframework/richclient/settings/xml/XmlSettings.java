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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.richclient.settings.AbstractSettings;
import org.springframework.richclient.settings.Settings;
import org.w3c.dom.Document;

/**
 * @author Peter De Bruycker
 */
public class XmlSettings extends AbstractSettings {
	private Document document;

	XmlSettings(Settings parent, String name) {
		super(parent, name);
	}

	XmlSettings() throws ParserConfigurationException, FactoryConfigurationError {
		super(null, "");

		// I am the root, so create a new document
		document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

	protected boolean internalContains(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	protected void internalSet(String key, String value) {
		// TODO Auto-generated method stub

	}

	protected String internalGet(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save() throws IOException {
		// TODO Auto-generated method stub

	}

	public void load() throws IOException {
		// TODO Auto-generated method stub

	}

	public Settings getSettings(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void internalRemove(String key) {
		// TODO Auto-generated method stub

	}

}
