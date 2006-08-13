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

import javax.swing.JComponent;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.settings.Settings;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.util.Assert;

public abstract class PreferencePage extends AbstractDialogPage {

	private boolean createApplyAndDefaultButtons = true;

	private PreferencePage parent;

	private PreferenceDialog preferenceDialog;

	private ActionCommand restoreDefaultsCommand;

	private ActionCommand applyCommand;

	public PreferencePage(String id) {
		super(id);
	}

    public PreferencePage(String id, boolean autoconfigure) {
        super(id, autoconfigure);
    }

    /**
	 * @return array containing "Restore defaults" and "Apply" commands
	 */
	protected ActionCommand[] getCommands() {
		return new ActionCommand[] { getRestoreDefaultsCommand(), getApplyCommand() };
	}

    /**
     * Will create "Apply" command if it doesn't exist yet
     * 
     * @return apply command.
     */
    protected ActionCommand getApplyCommand()
    {
        if (applyCommand == null) {
            applyCommand = new ActionCommand("applyCommand") {
    			public void doExecuteCommand() {
    				onApply();
    			}
    		};
        }
        return applyCommand;
    }

    /**
     * Will create "Restore Defaults" command if it doesn't exist yet
     * 
     * @return restore defaults command.
     */
    protected ActionCommand getRestoreDefaultsCommand()
    {
        if (restoreDefaultsCommand == null){
            restoreDefaultsCommand = new ActionCommand("restoreDefaultsCommand") {
    			public void doExecuteCommand() {
    				onDefaults();
    			}
    		};
        }
        return restoreDefaultsCommand;
    }

	/**
	 * Creates two commands "Restore defaults" and "Apply" for this page,
	 * layouts them on the panel.
	 * 
	 * @return panel containing "Restore defaults" and "Apply" commands
	 */
	protected JComponent createButtons() {
		CommandGroup commandGroup = CommandGroup.createCommandGroup(null,
				getCommands());
		JComponent buttonBar = commandGroup.createButtonBar();
		GuiStandardUtils.attachDialogBorder(buttonBar);

		return buttonBar;
	}

	protected abstract JComponent createContents();

	protected JComponent createControl() {
		GridBagLayoutBuilder builder = new GridBagLayoutBuilder();

		JComponent buttonPanel = null;
		if (createApplyAndDefaultButtons) {
			buttonPanel = createButtons();
		}

		JComponent contents = createContents();
		Assert.notNull(contents, "Contents cannot be null.");
		builder.append(contents, 1, 1, true, true);

		if (createApplyAndDefaultButtons) {
			builder.nextLine();
			builder.append(buttonPanel);
		}

		return builder.getPanel();
	}

	public PreferencePage getParent() {
		return parent;
	}

	protected Settings getSettings() {
		return preferenceDialog.getSettings();
	}

	/**
	 * Must store the preference values in the PreferenceStore. Does not save
	 * the PreferenceStore. Subclasses should override this method.
	 */
	protected void onApply() {
		onFinish();
	}

	protected void onDefaults() {
	}

	/**
	 * Notification that the user clicked the OK button on the PreferenceDialog.
	 */
	protected boolean onFinish() {
		return true;
	}

	public void setCreateApplyAndDefaultButtons(boolean create) {
		createApplyAndDefaultButtons = create;
	}

	public boolean getCreateApplyAndDefaultButtons() {
		return createApplyAndDefaultButtons;
	}

	public void setParent(PreferencePage parent) {
		this.parent = parent;
	}

	public void setPreferenceDialog(PreferenceDialog dialog) {
		Assert.notNull(dialog);
		preferenceDialog = dialog;
	}

	public void setPageComplete(boolean pageComplete) {
		if (applyCommand != null) {
			applyCommand.setEnabled(pageComplete);
		}

		super.setPageComplete(pageComplete);
	}
}