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
import java.util.HashMap;
import java.util.Map;


/**
 * Helper class, essential when testing with settings.
 * @author Peter De Bruycker
 */
public class TransientSettings extends AbstractSettings {

    private Map values = new HashMap();

    public TransientSettings() {
        this(null, "");
    }

    public TransientSettings(TransientSettings parent, String name) {
        super(parent, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#internalSet(java.lang.String,
     *      java.lang.String)
     */
    protected void internalSet(String key, String value) {
        values.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#internalGet(java.lang.String)
     */
    protected String internalGet(String key) {
        return (String) values.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#getKeys()
     */
    public String[] getKeys() {
        return (String[]) values.keySet().toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#save()
     */
    public void save() throws IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#load()
     */
    public void load() throws IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#getSettings(java.lang.String)
     */
    public Settings getSettings(String name) {
        return new TransientSettings(this, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.richclient.settings.AbstractSettings#internalRemove(java.lang.String)
     */
    protected void internalRemove(String key) {
        values.remove(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.richclient.settings.AbstractSettings#internalContains(java.lang.String)
     */
    protected boolean internalContains(String key) {
        return values.containsKey(key);
    }
}