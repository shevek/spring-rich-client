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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.ToStringCreator;

/**
 * A parameter object for a labelable component; consists of the text, mnemonic,
 * mnemonicIndex, and accelerator that may be associated with a labeled
 * component. This class also acts a factory for producing control prototypes
 * which are preconfigured with a LabelInfo's properties.
 * 
 * @author Keith Donald
 */
public class LabelInfo {
    private static final Log logger = LogFactory.getLog(LabelInfo.class);

    private String text = "";

    private int mnemonic;

    private int mnemonicIndex;

    public LabelInfo(String text) {
        this(text, 0, 0);
    }

    public LabelInfo(String text, int mnemonic) {
        this(text, mnemonic, 0);
    }

    public LabelInfo(String text, int mnemonic, int mnemonicIndex) {
        Assert.notNull(text);
        this.text = text;
        if (!StringUtils.hasText(text)) {
            mnemonicIndex = -1;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Constructing label info, properties: text='" + text
                    + "', mnemonic=" + mnemonic + ", mnemonicIndex="
                    + mnemonicIndex);
        }
        Assert.isTrue(mnemonic >= 0 && mnemonicIndex >= -1);
        Assert.isTrue(mnemonicIndex < text.length(),
                "The mnemonic index cannot be greater than the text length.");
        this.mnemonic = mnemonic;
        this.mnemonicIndex = mnemonicIndex;
    }

    public int hashCode() {
        return mnemonic + mnemonicIndex + text.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof LabelInfo)) { return false; }
        LabelInfo info = (LabelInfo)o;
        return text.equals(info.text) && mnemonic == info.mnemonic
                && mnemonicIndex == info.mnemonicIndex;
    }

    public JLabel configureLabel(JLabel label) {
        Assert.notNull(label);
        label.setText(text);
        label.setDisplayedMnemonic(getMnemonic());
        int index = getMnemonicIndex();
        if (index > 0) {
            label.setDisplayedMnemonicIndex(index);
        }
        return label;
    }

    public JLabel configureLabelFor(JLabel label, JComponent component) {
        configureLabel(label);
        if (!(component instanceof JPanel)) {
            String labelText = label.getText();
            if (!labelText.endsWith(":")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Appending colon to text field label text '"
                            + text + "'");
                }
                label.setText(labelText += ":");
            }
        }
        label.setLabelFor(component);
        return label;
    }

    public String getText() {
        return text;
    }

    public int getMnemonic() {
        return mnemonic;
    }

    public int getMnemonicIndex() {
        return mnemonicIndex;
    }

    public String toString() {
        return new ToStringCreator(this).appendProperties().toString();
    }
}