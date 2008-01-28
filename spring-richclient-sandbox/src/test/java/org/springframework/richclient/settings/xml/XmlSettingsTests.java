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
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsAbstractTests;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Peter De Bruycker
 */
public class XmlSettingsTests extends SettingsAbstractTests {

	protected Settings createSettings() throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element element = doc.createElement("settings");
		element.setAttribute("name", "root");
		doc.appendChild(element);

		return new XmlSettings(element);
	}

	public void testConstructor() throws ParserConfigurationException, FactoryConfigurationError, SAXException,
			IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<settings name=\"test-settings\">");
		sb.append("  <entry key=\"key-1\" value=\"value-1\" />");
		sb.append("  <entry key=\"key-2\" value=\"false\" />");
		sb.append("  <entry key=\"key-3\" value=\"1.5\" />");
		sb.append("  <settings name=\"child-settings\">");
		sb.append("    <entry key=\"child-key\" value=\"value\" />");
		sb.append("  </settings>");
		sb.append("</settings>");

		Element element = createElement(sb.toString());

		XmlSettings settings = new XmlSettings(null, element);
		assertEquals("test-settings", settings.getName());
		assertEquals(element, settings.getElement());

		List keys = Arrays.asList(settings.getKeys());
		assertEquals(3, keys.size());
		assertTrue(keys.contains("key-1"));
		assertTrue(keys.contains("key-2"));
		assertTrue(keys.contains("key-3"));

		assertEquals("value-1", settings.getString("key-1"));
		assertFalse(settings.getBoolean("key-2"));
		assertEquals(1.5f, settings.getFloat("key-3"), 0.0f);

		Settings childSettings = settings.getSettings("child-settings");
		assertTrue(childSettings instanceof XmlSettings);
		assertEquals(1, childSettings.getKeys().length);
		assertEquals("child-key", childSettings.getKeys()[0]);
	}

	public void testRemove_RemovesElement() throws ParserConfigurationException, FactoryConfigurationError,
			SAXException, IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<settings name=\"test-settings\">");
		sb.append("  <entry key=\"key-1\" value=\"value-1\" />");
		sb.append("  <entry key=\"key-2\" value=\"false\" />");
		sb.append("  <entry key=\"key-3\" value=\"1.5\" />");
		sb.append("</settings>");

		Element element = createElement(sb.toString());

		XmlSettings settings = new XmlSettings(null, element);

		assertTrue(settings.contains("key-2"));

		settings.remove("key-2");

		Element settingsElement = settings.getElement();
		NodeList childNodes = settingsElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node instanceof Element && node.getNodeName().equals("entry")) {
				Element tmp = (Element) node;
				assertFalse(tmp.getAttribute("key").equals("key-2"));
			}
		}
	}

	public void testSetValue() throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element element = doc.createElement("settings");
		element.setAttribute("name", "test-settings");
		doc.appendChild(element);
		Element entry = doc.createElement("entry");
		entry.setAttribute("key", "_key");
		entry.setAttribute("value", "_value");
		element.appendChild(entry);

		XmlSettings settings = new XmlSettings(null, element);

		settings.setString("_key", "new value");
		assertEquals("new value", entry.getAttribute("value"));
	}

	public void testSave() throws ParserConfigurationException, FactoryConfigurationError, IOException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element parentElement = doc.createElement("settings");
		parentElement.setAttribute("name", "parent-settings");
		doc.appendChild(parentElement);
		Element childElement = doc.createElement("settings");
		childElement.setAttribute("name", "child-settings");
		parentElement.appendChild(childElement);

		TestableXmlSettingsReaderWriter readerWriter = new TestableXmlSettingsReaderWriter();
		RootXmlSettings parentSettings = new RootXmlSettings(doc, readerWriter);
		Settings childSettings = parentSettings.getSettings("child-settings");
		childSettings.save();

		assertEquals(parentSettings, readerWriter.lastWritten);
	}
    
    public void testChildSettings() throws ParserConfigurationException, FactoryConfigurationError, SAXException, IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<settings name=\"test-settings\">");
        sb.append("  <entry key=\"key-1\" value=\"value-1\" />");
        sb.append("  <entry key=\"key-2\" value=\"false\" />");
        sb.append("  <entry key=\"key-3\" value=\"1.5\" />");
        sb.append("  <settings name=\"child-settings\">");
        sb.append("    <entry key=\"child-key\" value=\"value\" />");
        sb.append("  </settings>");
        sb.append("</settings>");

        XmlSettings settings = new XmlSettings(createElement(sb.toString()));
        
        assertEquals(Arrays.asList(new String[] {"child-settings"}), Arrays.asList(settings.getChildSettings()));
    }

	private static Element createElement(String xml) throws ParserConfigurationException, FactoryConfigurationError,
			SAXException, IOException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}

}
