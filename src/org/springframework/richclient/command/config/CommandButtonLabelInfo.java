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
package org.springframework.richclient.command.config;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.springframework.richclient.factory.ButtonConfigurer;
import org.springframework.richclient.factory.LabelInfo;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ToStringCreator;

/**
 * @author Keith Donald
 */
public class CommandButtonLabelInfo implements ButtonConfigurer {

    private LabelInfo labelInfo;

    private KeyStroke accelerator;

    public CommandButtonLabelInfo(String text) {
        this(new LabelInfo(text), null);
    }

    public CommandButtonLabelInfo(LabelInfo labelInfo, KeyStroke accelerator) {
        Assert.notNull(labelInfo);
        this.labelInfo = labelInfo;
        this.accelerator = accelerator;
    }

    public String getText() {
        return labelInfo.getText();
    }

    public int getMnemonic() {
        return labelInfo.getMnemonic();
    }

    public int getMnemonicIndex() {
        return labelInfo.getMnemonicIndex();
    }

    public KeyStroke getAccelerator() {
        return accelerator;
    }

    public int hashCode() {
        return labelInfo.hashCode()
                + (accelerator != null ? accelerator.hashCode() : 0);
    }

    public boolean equals(Object o) {
        if (!(o instanceof CommandButtonLabelInfo)) { return false; }
        CommandButtonLabelInfo info = (CommandButtonLabelInfo)o;
        return labelInfo.equals(info.labelInfo)
                && ObjectUtils.nullSafeEquals(accelerator, info.accelerator);
    }

    /**
     * Configures an existing button appropriately based on this label info's
     * properties.
     * 
     * @param button
     */
    public AbstractButton configure(AbstractButton button) {
        Assert.notNull(button);
        button.setText(labelInfo.getText());
        button.setMnemonic(labelInfo.getMnemonic());
        button.setDisplayedMnemonicIndex(labelInfo.getMnemonicIndex());
        configureAccelerator(button, getAccelerator());
        return button;
    }

    protected void configureAccelerator(AbstractButton button,
            KeyStroke accelerator) {
        if ((button instanceof JMenuItem) && !(button instanceof JMenu)) {
            ((JMenuItem)button).setAccelerator(accelerator);
        }
    }

    public String toString() {
        return new ToStringCreator(this).appendProperties().toString();
    }
}