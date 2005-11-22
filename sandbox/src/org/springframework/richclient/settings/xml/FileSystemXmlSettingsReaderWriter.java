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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.richclient.settings.SettingsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * <code>XmlSettingsReaderWriter</code> implementation that reads and writes
 * the xml from and to the file system.
 * 
 * @author Peter De Bruycker
 * 
 */
public class FileSystemXmlSettingsReaderWriter implements XmlSettingsReaderWriter {

	private String location;

	/**
	 * Creates a new instance.
	 * 
	 * @param location
	 *            the location where the xml files will be located
	 */
	public FileSystemXmlSettingsReaderWriter(String location) {
		this.location = location;
	}

	public void write(RootXmlSettings settings) throws SettingsException {
		try {
			File file = createFile(settings.getName());
            file.getParentFile().mkdirs();

			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(settings.getDocument()),
					new StreamResult(new FileOutputStream(file)));
		} catch (TransformerConfigurationException e) {
			throw new SettingsException("Unable to write document", e);
		} catch (TransformerException e) {
			throw new SettingsException("Unable to write document", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new SettingsException("Unable to write document", e);
		} catch (FileNotFoundException e) {
			throw new SettingsException("Unable to write document", e);
		}
	}

	/*
	 * TODO: create DTD + validate parsing
	 */
	public RootXmlSettings read(String key) throws SettingsException {
		try {
			File file = createFile(key);
			Document doc = null;
			if (file.exists()) {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
			} else {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element element = doc.createElement("settings");
				element.setAttribute("name", key);
				doc.appendChild(element);
			}

			RootXmlSettings settings = new RootXmlSettings(doc, this);

			return settings;
		} catch (SAXException e) {
			throw new SettingsException("Unable to read xml", e);
		} catch (IOException e) {
			throw new SettingsException("Unable to read xml", e);
		} catch (ParserConfigurationException e) {
			throw new SettingsException("Unable to read xml", e);
		} catch (FactoryConfigurationError e) {
			throw new SettingsException("Unable to read xml", e);
		}
	}

	private File createFile(String key) {
		return new File(location, key + ".settings.xml");
	}

	/**
	 * Returns the current location.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 * 
	 * @param location
	 *            the new location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

}
