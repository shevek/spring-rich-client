/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.image;

/**
 * Interface to be implemented by icon registries which can reload their icons
 * with a new size dynamically.
 * <p>
 * This allow accessibilty preferences to be applied by switching between small
 * and large icons, for example.
 * 
 * @author Keith Donald
 */
public interface SizedIconSource extends IconSource {

    /**
     * Reload all icons in this registry with the provided <code>IconSize</code>.
     * 
     * @param size
     *            The icon size.
     */
    public void reload(IconSize size);
}