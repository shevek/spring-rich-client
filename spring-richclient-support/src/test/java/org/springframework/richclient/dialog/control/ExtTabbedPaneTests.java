/*
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.richclient.dialog.control;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.richclient.util.EventListenerListHelper.EventBroadcastException;

import junit.framework.TestCase;

/**
 * 
 * @author Peter De Bruycker
 */
public class ExtTabbedPaneTests extends TestCase {
    // testcase for RCP-528
    public void testGetTabInsideChangeHandlerThrowsIndexOutOfBoundsException() {
        final ExtTabbedPane extTabbedPane = new ExtTabbedPane();

        // when the changelistener performs a getTab(index) call, an IndexOutOfBoundsException
        extTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tabbedPane = (JTabbedPane) extTabbedPane.getControl();
                int index = tabbedPane.getSelectedIndex();

                if (index >= 0) {
                    index = extTabbedPane.convertUIIndexToModelIndex(index);
                    Tab tab = extTabbedPane.getTab(index);
                    assertNotNull(tab);
                }
            }
        });

        Tab tab1 = new Tab("test1", new JLabel("test1"));
        Tab tab2 = new Tab("test2", new JLabel("test2"));
        
        try {
            extTabbedPane.addTab(tab1);
        }
        catch (EventBroadcastException e) {
            fail(e.getMessage());
        }


        try {
            extTabbedPane.addTab(0, tab2);
        }
        catch (EventBroadcastException e) {
            fail(e.getMessage());
        }
        
        extTabbedPane.selectTab(tab1);

        try {
            extTabbedPane.removeTab(tab1);
        }
        catch (EventBroadcastException e) {
            fail(e.getMessage());
        }
}
}
