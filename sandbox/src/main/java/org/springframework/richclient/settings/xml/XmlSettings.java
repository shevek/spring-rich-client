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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.richclient.settings.AbstractSettings;
import org.springframework.richclient.settings.Settings;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Peter De Bruycker
 */
public class XmlSettings extends AbstractSettings {
    private Element element;

    private boolean entriesLoaded;

    private Map values = new HashMap();

    public XmlSettings(Settings parent, Element element) {
        super(parent, getName(element));
        this.element = element;
    }

    public XmlSettings(Element element) {
        this(null, element);
    }

    private static String getName(Element element) {
        verifyElement(element);

        return element.getAttribute("name");
    }

    private static void verifyElement(Element element) {
        Assert.notNull(element, "element cannot be null");
        Assert.isTrue(element.getNodeName().equals("settings"), "element must be settings");
        Assert.isTrue(element.hasAttribute("name"), "element must have name attribute");
    }

    protected Settings internalCreateChild(String key) {
        loadChildrenIfNecessary();

        Element childElement = null;

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element tmp = (Element) node;
                if (tmp.getNodeName().equals("settings") && tmp.getAttribute("name").equals(key)) {
                    childElement = tmp;
                }
            }
        }

        if (childElement == null) {
            childElement = element.getOwnerDocument().createElement("settings");
            childElement.setAttribute("name", key);
            element.appendChild(childElement);
        }

        return new XmlSettings(this, childElement);
    }

    protected boolean internalContains(String key) {
        loadChildrenIfNecessary();

        return values.containsKey(key);
    }

    protected void internalSet(String key, String value) {
        loadChildrenIfNecessary();

        Element entry = findOrCreateEntry(key);
        entry.setAttribute("value", value);

        values.put(key, value);
    }

    private Element findEntry(String key) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element && node.getNodeName().equals("entry")) {
                Element tmp = (Element) node;
                if (tmp.getAttribute("key").equals(key)) {
                    return tmp;
                }
            }
        }

        return null;
    }

    private Element findOrCreateEntry(String key) {
        Element entry = findEntry(key);
        if (entry == null) {
            entry = element.getOwnerDocument().createElement("entry");
            entry.setAttribute("key", key);
            element.appendChild(entry);
        }

        return entry;
    }

    protected String internalGet(String key) {
        loadChildrenIfNecessary();

        return (String) values.get(key);
    }

    public String[] getKeys() {
        loadChildrenIfNecessary();

        return (String[]) values.keySet().toArray(new String[values.size()]);
    }

    public void save() throws IOException {
        getParent().save();
    }

    private void loadChildrenIfNecessary() {
        if (!entriesLoaded) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    Element el = (Element) node;
                    if (el.getNodeName().equals("entry")) {
                        // entry
                        values.put(el.getAttribute("key"), el.getAttribute("value"));
                    }
                }
            }

            entriesLoaded = true;
        }
    }

    public void load() throws IOException {
        loadChildrenIfNecessary();
    }

    protected void internalRemove(String key) {
        loadChildrenIfNecessary();

        Element entry = findEntry(key);
        if (entry != null) {
            element.removeChild(entry);
        }

        values.remove(key);
    }

    public Element getElement() {
        return element;
    }

    protected String[] internalGetChildSettings() {
        List childSettingsNames = new ArrayList();

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element el = (Element) node;
                if (el.getNodeName().equals("settings")) {
                    childSettingsNames.add(el.getAttribute("name"));
                }
            }
        }

        return (String[]) childSettingsNames.toArray(new String[childSettingsNames.size()]);
    }

    public void internalRemoveSettings() {
        element.getParentNode().removeChild(element);
    }
}
