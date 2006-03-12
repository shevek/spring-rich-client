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
package org.springframework.richclient.dialog.selection;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.text.TextComponentPopup;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;
import ca.odell.glazedlists.swing.TextFilterList;

/**
 * A <code>ListSelectionDialog</code> can be used to select an item from a list.
 * <br/>
 * @author peter.de.bruycker
 */
public abstract class ListSelectionDialog extends ApplicationDialog {

    private String description;

    private ListCellRenderer renderer;

    private JList list;

    private EventList eventList;

    public ListSelectionDialog(String title, Window parent, List items) {
        this(title, parent, new BasicEventList(items));
    }

    public ListSelectionDialog(String title, Window parent, EventList eventList) {
        super(title, parent);
        this.eventList = eventList;
    }

    public void setDescription(String desc) {
        Assert.isTrue(!isControlCreated(), "Set the description before the control is created.");

        description = desc;
    }

    public void setRenderer(ListCellRenderer renderer) {
        Assert.notNull(renderer, "Renderer cannot be null.");
        Assert.isTrue(!isControlCreated(), "Install the renderer before the control is created.");

        this.renderer = renderer;
    }

    /** 
     * @see org.springframework.richclient.dialog.ApplicationDialog#createDialogContentPane()
     */
    protected JComponent createDialogContentPane() {
        createListControl();
        
        JTextField filter = null;        
        
        if(eventList instanceof TextFilterList) {
            filter = ((TextFilterList)eventList).getFilterEdit();
            TextComponentPopup.attachPopup(filter);
            filter.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        // transfer focus to list
                        list.requestFocusInWindow();
                    }
                }
            });
        }
        
        setFinishEnabled(false);

        if (!eventList.isEmpty()) {
            list.setSelectedIndex(0);
        }

        TableLayoutBuilder builder = new TableLayoutBuilder();

        if (StringUtils.hasText(description)) {
            if (filter != null) {
                builder.cell(getComponentFactory().createLabelFor(description, filter));
            }
            else {
                builder.cell(getComponentFactory().createLabelFor(description, list));
            }
            builder.row();
        }

        if (filter != null) {
            builder.cell(filter);
            builder.row();
        }

        builder.cell(new JScrollPane(list));

        return builder.getPanel();
    }

    private void createListControl() {
        list = new JList(new EventListModel(eventList));

        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {

            private int lastIndex = -1;

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (list.getSelectionModel().isSelectionEmpty() && lastIndex > -1) {
                    if(list.getModel().getSize() > 0) {
                        list.setSelectedIndex(lastIndex);
                        return;
                    }
                }

                setFinishEnabled(!list.getSelectionModel().isSelectionEmpty());
                lastIndex = list.getSelectedIndex();
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    getFinishCommand().execute();
                }
            }
        });

        if (renderer != null) {
            list.setCellRenderer(renderer);
        }
    }

    /** 
     * @see org.springframework.richclient.dialog.ApplicationDialog#onFinish()
     */
    protected boolean onFinish() {
        onSelect(getSelectedObject());
        return true;
    }

    private Object getSelectedObject() {
        return eventList.get(list.getSelectedIndex());
    }

    protected abstract void onSelect(Object selection);
}