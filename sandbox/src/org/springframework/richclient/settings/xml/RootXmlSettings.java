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

import org.springframework.richclient.settings.SettingsException;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Peter De Bruycker
 */
public class RootXmlSettings extends XmlSettings {
	private XmlSettingsReaderWriter readerWriter;

	private Document doc;

	public RootXmlSettings(Document doc, XmlSettingsReaderWriter readerWriter) {
		super(getSettingsElement(doc));

		this.doc = doc;

		Assert.notNull(readerWriter, "XmlSettingsReaderWriter cannot be null");
		this.readerWriter = readerWriter;
	}

	private static Element getSettingsElement(Document doc) {
		Assert.notNull(doc, "Document cannot be null");
		return doc.getDocumentElement();
	}

	public void save() throws IOException {
		try {
			readerWriter.write(this);
		} catch (SettingsException e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		return doc;
	}
}
