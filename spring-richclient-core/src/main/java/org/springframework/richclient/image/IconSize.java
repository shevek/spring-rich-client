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

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Enum for various supported icon sizes.
 * 
 * @author Keith Donald
 */
public class IconSize {
    private String name;

    private int pixels;

    /**
     * The standard 16 pixel "small" icon.
     */
    public static final IconSize SMALL = new IconSize("small", 16);

    /**
     * The standard 24 pixel "large" icon.
     */
    public static final IconSize LARGE = new IconSize("large", 24);

    private IconSize(String name, int value) {
        Assert.isTrue(StringUtils.hasText(name));
        this.name = name;
        this.pixels = value;
    }

    /**
     * Returns the icon size name.
     * 
     * @return The logical name of the icon size.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the size value in pixels.
     * 
     * @return The value in pixels.
     */
    public int getValue() {
        return pixels;
    }

    public String toString() {
        return "[IconSize name = '" + getName() + "', value = " + getValue() + "]";
    }
}