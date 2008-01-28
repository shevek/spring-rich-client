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
package org.springframework.richclient.selection.dialog;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.text.TextComponentPopup;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

/**
 * Filtered <code>ListSelectionDialog</code>.
 * 
 * @author Peter De Bruycker
 */
public class FilterListSelectionDialog extends ListSelectionDialog {

    private TextFilterator filterator;

    private FilterList filterList;

    public void setFilterator(TextFilterator filterator) {
        this.filterator = filterator;
    }

    public FilterListSelectionDialog(String title, Window parent, FilterList filterList) {
        super(title, parent, filterList);
        this.filterList = filterList;
    }

    protected JComponent createSelectionComponent() {
        TableLayoutBuilder builder = new TableLayoutBuilder();

        JComponent filterComponent = createFilterComponent();
        builder.cell(filterComponent);
        builder.row();
        builder.relatedGapRow();
        builder.cell(super.createSelectionComponent());

        return builder.getPanel();
    }

    protected JComponent createFilterComponent() {
        JTextField filter = new JTextField();

        filterList.setMatcherEditor(new TextComponentMatcherEditor(filter, filterator));

        TextComponentPopup.attachPopup(filter);
        filter.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    // transfer focus to list
                    getList().requestFocusInWindow();
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (getFinishCommand().isEnabled())
                        getFinishCommand().execute();
                }
            }
        });

        return filter;
    }
}
