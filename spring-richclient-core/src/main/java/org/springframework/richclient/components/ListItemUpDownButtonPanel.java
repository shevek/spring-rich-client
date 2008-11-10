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
package org.springframework.richclient.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.springframework.richclient.core.UIConstants;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class ListItemUpDownButtonPanel extends AbstractControlFactory {

    private static final String DOWN_BUTTON_MESSAGE_CODE = "button.down";

    private static final String UP_BUTTON_MESSAGE_CODE = "button.up";
    
    private final ListSelectionListener listSelectionChangeHandler = new ListSelectionChangeHandler();

    private final UpAction upAction = new UpAction();

    private final DownAction downAction = new DownAction();

    private final JList list;

    private JButton upButton;

    private JButton downButton;

    public ListItemUpDownButtonPanel(JList list) {
        this.list = list;
        Assert.isTrue(list.getModel() instanceof List, "List model must implement the List collection interface");
        subscribe();
    }

    protected JList getList() {
        return list;
    }

    protected List getListModel() {
        return (List)list.getModel();
    }

    protected JComponent createControl() {
        return createUpDownButtonPanel();
    }

    private void subscribe() {
        this.list.addListSelectionListener(listSelectionChangeHandler);
    }

    protected void onEmptySelection() {
        upButton.setEnabled(false);
        downButton.setEnabled(false);
    }

    protected void onSelection() {
        if (!isContiguousSelection(getList())) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
        else {
            if (getList().getMinSelectionIndex() == 0) {
                upButton.setEnabled(false);
            }
            else {
                upButton.setEnabled(true);
            }
            if (getList().getMaxSelectionIndex() == (getListModel().size() - 1)) {
                downButton.setEnabled(false);
            }
            else {
                downButton.setEnabled(true);
            }
        }
    }

    private boolean isContiguousSelection(JList list) {
        for (int i = list.getMinSelectionIndex(); i <= list.getMaxSelectionIndex(); i++) {
            if (!list.isSelectedIndex(i)) {
                return false;
            }
        }
        return true;
    }

    private JComponent createUpDownButtonPanel() {
        upButton = getComponentFactory().createButton(UP_BUTTON_MESSAGE_CODE);
        upButton.setEnabled(false);
        upButton.addActionListener(upAction);

        downButton = getComponentFactory().createButton(DOWN_BUTTON_MESSAGE_CODE);
        downButton.setEnabled(false);
        downButton.addActionListener(downAction);

        JComponent panel = GuiStandardUtils.createCommandButtonColumn(new JButton[] {upButton, downButton});
        panel.setBorder(GuiStandardUtils.createLeftAndRightBorder(UIConstants.ONE_SPACE));
        return panel;
    }

    private class ListSelectionChangeHandler implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                if (list.isSelectionEmpty()) {
                    onEmptySelection();
                }
                else {
                    onSelection();
                }
            }
        }
    }

    private class DownAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int[] indices = getList().getSelectedIndices();
            Object[] array = new Object[indices.length];
            Arrays.sort(indices);
            List model = getListModel();
            if (indices[indices.length - 1] == model.size() - 1) {
                return;
            }
            for (int i = 0; i < indices.length; i++) {
                array[i] = model.get(indices[i] - i);
                model.remove(indices[i] - i);
            }
            int[] newIndices = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                int newIndex = indices[0] + 1 + i;
                model.add(newIndex, array[i]);
                newIndices[i] = newIndex;
            }
            getList().setSelectedIndices(newIndices);
        }
    }

    private class UpAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int[] indices = getList().getSelectedIndices();
            Object[] array = new Object[indices.length];
            Arrays.sort(indices);
            if (indices[0] == 0) {
                return;
            }
            List model = getListModel();
            for (int i = 0; i < indices.length; i++) {
                array[i] = model.get(indices[i] - i);
                model.remove(indices[i] - i);
            }
            int[] newIndices = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                int newIndex = indices[0] - 1 + i;
                model.add(newIndex, array[i]);
                newIndices[i] = newIndex;
            }
            getList().setSelectedIndices(newIndices);
        }
    }
}