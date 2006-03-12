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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.richclient.settings.SettingsException;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Helper class, used for testing. The read method returns the xml settings
 * parsed from the xml passed in the constructor, the write method writes to a
 * buffer which can be read with the getBuffer method.
 * 
 * @author Peter De Bruycker
 */
public class StringXmlSettingsReaderWriter implements XmlSettingsReaderWriter {

	private String xml;

	private StringWriter buffer;

	/**
	 * Creates a new instance. The xml will be parsed when the read method is
	 * invoked.
	 * 
	 * @param xml
	 *            the xml
	 */
	public StringXmlSettingsReaderWriter(String xml) {
		this.xml = xml;
	}

	/**
	 * Creates a new instance.
	 */
	public StringXmlSettingsReaderWriter() {
		this(null);
	}

	public void write(RootXmlSettings settings) throws SettingsException {
		try {
			buffer = new StringWriter();
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(settings.getDocument()),
					new StreamResult(buffer));
		} catch (Exception e) {
			throw new SettingsException("Unable to write xml", e);
		}
	}

	/**
	 * Returns the buffered xml.
	 * 
	 * @return the buffered xml
	 */
	public String getBuffer() {
		return buffer.getBuffer().toString();
	}

	public RootXmlSettings read(String key) throws SettingsException {
		try {
			Document doc = null;
			if (StringUtils.hasText(xml)) {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
						new InputSource(new StringReader(xml)));
			} else {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element element = doc.createElement("settings");
				element.setAttribute("name", key);
				doc.appendChild(element);
			}

			return new RootXmlSettings(doc, this);
		} catch (Exception e) {
			throw new SettingsException("Unable to parse xml " + xml, e);
		}
	}

}
