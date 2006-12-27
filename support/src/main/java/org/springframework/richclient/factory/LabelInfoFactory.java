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
package org.springframework.richclient.factory;

import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.config.CommandButtonLabelInfo;
import org.springframework.util.StringUtils;

/**
 * A factory for creating a {@link LabelInfo} parameter object from a given string descriptor.
 * The string is formatted as follows:
 * 
 * <pre>
 *   
 *   te&amp;xt@ctrl-T
 *   
 * </pre>
 * 
 * Where "&" represents the label's mnemonic and implied mnemonic index, and @&lt;accelerator&gt;
 * represents a key stroke accelerator to be set when the {@code LabelInfo} is applied
 * to clickable buttons.
 * 
 * @author Keith Donald
 */
public class LabelInfoFactory {
    
    private static final Log logger = LogFactory.getLog(LabelInfoFactory.class);

    public String encodedLabel;

    public static final CommandButtonLabelInfo BLANK_BUTTON_LABEL = new CommandButtonLabelInfo("commandLabel");

    /**
     * Creates a new uninitialized {@code LabelInfoFactory}.
     */
    public LabelInfoFactory() {
        //do nothing
    }

    /**
     * Creates a new {@code LabelInfoFactory} that will create {@link LabelInfo} instances based 
     * on the given string.
     *
     * @param encodedLabel The string that represents the label info parameters.
     */
    public LabelInfoFactory(String encodedLabel) {
        setLabel(encodedLabel);
    }

    /**
     * Sets the string that uses an encoded syntax to represent label info parameters.
     *
     * @param encodedLabel The encoded representation of a {@code LabelInfo}. May be null.
     */
    public void setLabel(String encodedLabel) {
        this.encodedLabel = encodedLabel;
    }

    /**
     * Create a new label info instance from the configured label string.
     * Returns a new instance on each invocation.
     * 
     * @return A new LabelInfo instance.
     */
    public LabelInfo createLabelInfo() {
        return parseLabelInfo(this.encodedLabel);
    }

    /**
     * Creates a new {@link CommandButtonLabelInfo} based on this instance's encoded label string. 
     *
     * @return A new CommandButtonLabelInfo.
     */
    public CommandButtonLabelInfo createButtonLabelInfo() {
        return parseButtonLabelInfo(this.encodedLabel);
    }

    /**
     * Create a new label info instance from the configured label string.
     * Returns a new instance on each invocation.
     * 
     * @return A new LabelInfo instance.
     */
    public static LabelInfo createLabelInfo(String encodedLabel) {
        return new LabelInfoFactory(encodedLabel).createLabelInfo();
    }

    /**
     * Create a new button label info instance from the configured label string.
     * Returns a new instance on each invocation.
     * 
     * @return A new LabelInfo instance.
     */
    public static CommandButtonLabelInfo createButtonLabelInfo(String encodedLabel) {
        if (StringUtils.hasText(encodedLabel))
            return new LabelInfoFactory(encodedLabel).createButtonLabelInfo();

        return BLANK_BUTTON_LABEL;
    }

    /**
     * Parses the text label and mnemonic from a string label descriptor.
     * <p>
     * The format of a label descriptor is: <code>;amp<text></code> where
     * ;amp (or &) is a token that states the letter following in the text
     * should be used as the mnemonic for the label. A "@" followed by an
     * properly formatted string KeyStroke will also set an accelerator key, if
     * this label labels an actionable button.
     * <p>
     * Examples: My Page, &File, &Save@ctrl S, Select &All@ctrl A
     * 
     * @param text
     *            the label descriptor
     */
    private LabelInfo parseLabelInfo(String text) {
        if (!StringUtils.hasText(text)) {
            return new LabelInfo("");
        }

        int mnemonic = 0;
        int mnemonicIndex = -1;

        for (; text != null;) {
            int i = text.indexOf('&');
            if (i == -1) {
                break;
            }
            if (i < text.length() - 1) {
                mnemonic = text.charAt(i + 1);
                mnemonicIndex = i;
                if (mnemonic >= 'a' && mnemonic <= 'z') {
                    mnemonic -= ('a' - 'A');
                }
            }
            text = text.substring(0, i) + text.substring(i + 1);
        }
        return new LabelInfo(text, mnemonic, mnemonicIndex);
    }

    private CommandButtonLabelInfo parseButtonLabelInfo(String text) {
        LabelInfo info = parseLabelInfo(text);
        text = info.getText();

        KeyStroke accelerator = null;

        int i = text.indexOf('@');
        if (i != -1) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found accelerator for label '" + text + "' at index " + i);
            }
            String keyStrokeString = text.substring(i + 1);
            accelerator = KeyStroke.getKeyStroke(keyStrokeString);
            if (accelerator == null && logger.isWarnEnabled()) {
                logger.warn("Specified action accelerator string '" + keyStrokeString
                        + "' did not translate to a valid KeyStroke.");
            }
            text = text.substring(0, i);
        }
        info = new LabelInfo(text, info.getMnemonic(), info.getMnemonicIndex());
        return new CommandButtonLabelInfo(info, accelerator);
    }
    
}
