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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * A empty icon is a blank icon useful for ensuring alignment in menus between
 * menuitems that have and do not have icons.
 * 
 * @author Keith Donald
 * @see javax.swing.Icon
 */
public final class EmptyIcon implements Icon {

    /**
     * Convenience object for small icons, whose size matches the size of small
     * icons in Sun's graphics repository.
     */
    public static final EmptyIcon SMALL = new EmptyIcon(IconSize.SMALL);

    /**
     * Convenience object for large icons, whose size matches the size of large
     * icons in Sun's graphics repository.
     */
    public static final EmptyIcon LARGE = new EmptyIcon(IconSize.LARGE);

    private IconSize size;

    /**
     * EmptyIcon objects are always square, having identical height and width.
     * 
     * @param size
     *            The size of the empty icon. Icons are always equal on all
     *            sides.
     */
    public EmptyIcon(IconSize size) {
        this.size = size;
    }

    public int getIconWidth() {
        return this.size.getValue();
    }

    public int getIconHeight() {
        return this.size.getValue();
    }

    /**
     * This implementation is empty, and paints nothing.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        //empty
    }
}