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
package org.springframework.richclient.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.core.GuardedGroup;
import org.springframework.richclient.factory.ComponentFactory;

public class ListUtils {

    private ListUtils() {

    }

    public static JButton createRemoveRowButton(ComponentFactory factory,
            final List list, final ValueModel selectionIndexHolder) {
        JButton removeButton = factory.createButton("label.remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = ((Integer)selectionIndexHolder
                        .getValue()).intValue();
                list.remove(selectedRowIndex);
            }
        });
        new SingleListSelectionGuard(selectionIndexHolder, GuardedGroup
                .createGuardedAdapter(removeButton));
        return removeButton;
    }

}