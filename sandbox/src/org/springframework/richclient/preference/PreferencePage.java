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
package org.springframework.richclient.preference;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.util.Assert;

/**
 * @author Peter De Bruycker
 */
public abstract class PreferencePage extends AbstractDialogPage {

    private JButton applyButton;

    private boolean createApplyAndDefaultButtons = true;

    private JButton defaultsButton;

    private PreferencePage parent;

    private PreferenceDialog preferenceDialog;

    public PreferencePage(String id) {
        super(id);
    }

    private JComponent createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        defaultsButton = new JButton("Restore defaults");
        defaultsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onDefaults();
            }
        });

        applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                onApply();
            }
        });

        panel.add(defaultsButton);
        panel.add(applyButton);

        return panel;
    }

    protected abstract JComponent createContents();

    protected final JComponent createControl() {
        GridBagLayoutBuilder builder = new GridBagLayoutBuilder();

        //    JPanel panel = new JPanel(new BorderLayout());

        JComponent contents = createContents();
        Assert.notNull(contents, "Contents cannot be null.");
        //    panel.add(contents);
        builder.append(contents, 1, 1, true, true);

        if (createApplyAndDefaultButtons) {
            builder.nextLine();
            builder.append(createButtons());
            //      panel.add(createButtons(), BorderLayout.SOUTH);
        }

        //    return panel;
        return builder.getPanel();
    }

    public PreferencePage getParent() {
        return parent;
    }

    protected PreferenceStore getPreferenceStore() {
        return preferenceDialog.getPreferenceStore();
    }

    /**
     * Must store the preference values in the PreferenceStore. Does not save
     * the PreferenceStore. Subclasses should override this method.
     */
    protected void onApply() {
    }

    protected void onDefaults() {
    }

    /**
     * Notification that the user clicked the OK button on the PreferenceDialog.
     */
    protected boolean onFinish() {
        onApply();
        return true;
    }

    public void setCreateApplyAndDefaultButtons(boolean create) {
        createApplyAndDefaultButtons = create;
    }

    public void setParent(PreferencePage parent) {
        this.parent = parent;
    }

    public void setPreferenceDialog(PreferenceDialog dialog) {
        Assert.notNull(dialog);
        preferenceDialog = dialog;
    }
}