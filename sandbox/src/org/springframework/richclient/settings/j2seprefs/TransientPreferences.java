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
package org.springframework.richclient.settings.j2seprefs;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Transient J2SE Preferences implementation. Used for testing with J2SE prefs.
 * 
 * @author Peter De Bruycker
 */
public class TransientPreferences extends AbstractPreferences {

	public TransientPreferences(AbstractPreferences parent, String name) {
		super(parent, name);
	}

	public TransientPreferences() {
		this(null, "");
	}

	private Map children = new HashMap();

	private Map values = new HashMap();

	protected void flushSpi() throws BackingStoreException {
		// not used
	}

	protected void removeNodeSpi() throws BackingStoreException {
		values.clear();
	}

	protected void syncSpi() throws BackingStoreException {
		// not used
	}

	protected String[] childrenNamesSpi() throws BackingStoreException {
		return (String[]) children.keySet().toArray(new String[children.size()]);
	}

	protected String[] keysSpi() throws BackingStoreException {
		return (String[]) values.keySet().toArray(new String[values.size()]);
	}

	protected void removeSpi(String key) {
		values.remove(key);
	}

	protected String getSpi(String key) {
		if (values.containsKey(key)) {
			return (String) values.get(key);
		}
		return "";
	}

	protected void putSpi(String key, String value) {
		values.put(key, value);
	}

	protected AbstractPreferences childSpi(String name) {
		if (!children.containsKey(name)) {
			children.put(name, new TransientPreferences(this, name));
		}
		return (AbstractPreferences) children.get(name);
	}
}
