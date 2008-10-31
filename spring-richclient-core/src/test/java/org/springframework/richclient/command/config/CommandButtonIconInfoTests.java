/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.command.config;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import junit.framework.TestCase;

import org.springframework.richclient.test.TestIcon;

/**
 * Testcase for CommandButtonIconInfo
 * 
 * @author Peter De Bruycker
 */
public class CommandButtonIconInfoTests extends TestCase {

    private Icon icon;

    private Icon selectedIcon;

    private Icon rolloverIcon;

    private Icon disabledIcon;

    private Icon pressedIcon;

    private CommandButtonIconInfo completeInfo;

    public void testConstructor() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon);
        assertEquals(icon, info.getIcon());
        assertNull(info.getSelectedIcon());
        assertNull(info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor2() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon, selectedIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertNull(info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor3() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertEquals(rolloverIcon, info.getRolloverIcon());
        assertNull(info.getDisabledIcon());
        assertNull(info.getPressedIcon());
    }

    public void testConstructor4() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon,
                pressedIcon);
        assertEquals(icon, info.getIcon());
        assertEquals(selectedIcon, info.getSelectedIcon());
        assertEquals(rolloverIcon, info.getRolloverIcon());
        assertEquals(disabledIcon, info.getDisabledIcon());
        assertEquals(pressedIcon, info.getPressedIcon());
    }

    public void testConfigureWithNullButton() {
        CommandButtonIconInfo info = new CommandButtonIconInfo(icon);
        try {
            info.configure(null);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureWithJButton() {
        JButton button = new JButton("Test");
        JButton result = (JButton)completeInfo.configure(button);
        assertSame(button, result);

        assertEquals(icon, button.getIcon());
        assertEquals(selectedIcon, button.getSelectedIcon());
        assertEquals(rolloverIcon, button.getRolloverIcon());
        assertEquals(disabledIcon, button.getDisabledIcon());
        assertEquals(pressedIcon, button.getPressedIcon());
    }

    public void testConfigureWithJMenuItem() {
        JMenuItem button = new JMenuItem("Test");
        JMenuItem result = (JMenuItem)completeInfo.configure(button);
        assertSame(button, result);

        assertEquals(icon, button.getIcon());
        assertEquals(selectedIcon, button.getSelectedIcon());
        assertEquals(rolloverIcon, button.getRolloverIcon());
        assertEquals(disabledIcon, button.getDisabledIcon());
        assertEquals(pressedIcon, button.getPressedIcon());
    }

    public void testConfigureWithJMenu() {
        JMenu button = new JMenu("Test");
        button.setIcon(icon);
        button.setSelectedIcon(selectedIcon);
        button.setRolloverIcon(rolloverIcon);
        button.setDisabledIcon(disabledIcon);
        button.setPressedIcon(pressedIcon);

        JMenuItem result = (JMenuItem)completeInfo.configure(button);
        assertSame(button, result);

        assertEquals(icon, button.getIcon());
        assertEquals(selectedIcon, button.getSelectedIcon());
        assertEquals(rolloverIcon, button.getRolloverIcon());
        assertEquals(disabledIcon, button.getDisabledIcon());
        assertEquals(pressedIcon, button.getPressedIcon());
    }

    private static void pass() {
        // test passes
    }

    protected void setUp() throws Exception {
        icon = new TestIcon(Color.BLUE);
        selectedIcon = new TestIcon(Color.BLACK);
        rolloverIcon = new TestIcon(Color.GREEN);
        disabledIcon = new TestIcon(Color.GRAY);
        pressedIcon = new TestIcon(Color.WHITE);

        completeInfo = new CommandButtonIconInfo(icon, selectedIcon, rolloverIcon, disabledIcon, pressedIcon);
    }
}