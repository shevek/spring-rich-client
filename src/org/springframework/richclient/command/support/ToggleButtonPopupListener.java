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
package org.springframework.richclient.command.support;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class ToggleButtonPopupListener implements PopupMenuListener,
        ItemListener {
    private JToggleButton button;

    private JPopupMenu menu;

    private boolean buttonWasPressed;

    private boolean shouldReopen = true;

    public static void bind(JToggleButton button, JPopupMenu menu) {
        new ToggleButtonPopupListener(button, menu);
    }

    private ToggleButtonPopupListener(JToggleButton button, JPopupMenu menu) {
        this.button = button;
        this.menu = menu;
        button.addItemListener(this);
        menu.addPopupMenuListener(this);
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                buttonWasPressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                buttonWasPressed = false;
            }
        });
    }

    public void itemStateChanged(ItemEvent e) {
        if (button.isSelected()) {
            menu.show(button, 0, button.getHeight());
        }
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        if (!buttonWasPressed) {
            button.setSelected(false);
        }
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

    }

    public void popupMenuCanceled(PopupMenuEvent e) {
    }
}