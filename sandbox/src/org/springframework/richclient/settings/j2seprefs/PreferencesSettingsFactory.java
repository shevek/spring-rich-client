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

import java.io.IOException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.settings.SettingsFactory;
import org.springframework.util.Assert;

/**
 * Settings factory that uses J2SE Preferences.
 * 
 * @author Peter De Bruycker
 */
public class PreferencesSettingsFactory implements SettingsFactory {

    private static final String INTERNAL = "internal";

    private static final String USER = "user";

    private Settings userSettings;

    private Settings internalSettings;

    private PreferencesFactory preferencesFactory;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PreferencesFactory getPreferencesFactory() {
        return preferencesFactory;
    }

    public void setPreferencesFactory(PreferencesFactory preferencesFactory) {
        this.preferencesFactory = preferencesFactory;
    }

    public Settings createInternalSettings() {
        if (internalSettings == null) {
            internalSettings = createSettings(INTERNAL);
        }
        return internalSettings;
    }

    public Settings createUserSettings() {
        if (userSettings == null) {
            userSettings = createSettings(USER);
        }
        return userSettings;
    }

    private Preferences getForId(Preferences root, String id) {
        Assert.notNull(root);
        Preferences result = root;
        String[] idParts = id.split("\\.");
        for (int i = 0; i < idParts.length; i++) {
            result = result.node(idParts[i]);
        }
        return result;
    }

    private Settings createSettings(String name) {
        Assert.hasText(id, "An id must be assigned.");
        Settings settings = null;
        if (preferencesFactory == null) {
            settings = new PreferencesSettings(getForId(Preferences.userRoot(), id).node(name));
        } else {
            settings = new PreferencesSettings(getForId(preferencesFactory.userRoot(), id).node(name));
        }

        try {
            settings.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return settings;
    }

}