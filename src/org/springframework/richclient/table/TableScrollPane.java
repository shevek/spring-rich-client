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
package org.springframework.richclient.table;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Keith Donald
 */
public class TableScrollPane {
    JScrollPane scrollPane;

    public TableScrollPane(JTable table, final TableUpdater tableUpdater) {
        this.scrollPane = new JScrollPane(table);
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BoundedRangeModel m = (BoundedRangeModel)(e.getSource());
                tableUpdater.setUpdatesEnabled(!(m.getValueIsAdjusting()));
            }
        };
        this.scrollPane.getVerticalScrollBar().getModel().addChangeListener(changeListener);
        this.scrollPane.getHorizontalScrollBar().getModel().addChangeListener(changeListener);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

}