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
package org.springframework.richclient.util;

import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.springframework.richclient.image.EmptyIcon;

public class MenuElementUtils {
    private MenuElementUtils() {
    }

    /**
     * Return whether there are any icons at the moment.
     * 
     * @return whether there are any icons at the moment
     */
    private static boolean hasIcons(MenuElement menuElement) {
        MenuElement[] elements = menuElement.getSubElements();
        for (int i = 0; i < elements.length; i++) {
            MenuElement element = elements[i];
            if (element instanceof JMenuItem && (((JMenuItem)element).getIcon() != null)) {
                return true;
            }
            if (element instanceof JPopupMenu) {
                return hasIcons(element);
            }
        }
        return false;
    }

    /**
     * Fill in icons (if there is no icon, put in an empty icon).
     */
    private static void fillInIcons(MenuElement menuElement) {
        MenuElement[] elements = menuElement.getSubElements();
        for (int i = 0; i < elements.length; i++) {
            MenuElement element = elements[i];
            if (element instanceof JMenuItem) {
                JMenuItem menu = (JMenuItem)element;
                if (menu.getIcon() == null) {
                    menu.setIcon(EmptyIcon.SMALL);
                }
            }
            if (element instanceof JPopupMenu) {
                fillInIcons(element);
            }
        }
    }

    /**
     * Align the icons.
     */
    public static void alignIcons(MenuElement menuElement) {
        // We fill in a blank icon to align the menu items
        if (hasIcons(menuElement)) {
            fillInIcons(menuElement);
        }
    }

    public static void showButtonPopupMenu(AbstractButton button, JPopupMenu popup) {
        if (!popup.isVisible()) {
            Dimension size = button.getSize();
            popup.show(button, 0, size.height);
        }
    }

}