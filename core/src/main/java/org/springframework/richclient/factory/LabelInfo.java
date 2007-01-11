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

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A parameter object for a labelable component; consists of the text, mnemonic, mnemonicIndex and 
 * accelerator that may be associated with a labeled component. This class also acts as a 
 * configurer for {@link JLabel}s, using the properties of .
 * 
 * @author Keith Donald
 * 
 * @deprecated Replaced by {@link org.springframework.richclient.core.LabelInfo}
 */
public class LabelInfo {
    private static final Log logger = LogFactory.getLog(LabelInfo.class);

    private final String text;

    private final int mnemonic;

    private final int mnemonicIndex;

    /**
     * Creates a new {@code LabelInfo} with the given text and no specified mnemonic.
     *
     * @param text The text to be displayed by the label. This may be an empty string but 
     * cannot be null.
     * 
     * @throws IllegalArgumentException if {@code text} is null.
     */
    public LabelInfo(String text) {
        this(text, 0, 0);
        //TODO isn't -1 supposed to be the default value for a mnemonic index?
    }

    /**
     * Creates a new {@code LabelInfo} with the given text and mnemonic character.
     *
     * @param text The text to be displayed by the label. This may be an empty string but cannot
     * be null.
     * @param mnemonic The character from the label text that acts as a mnemonic.
     * 
     * @throws IllegalArgumentException if {@code text} is null or if {@code mnemonic} is a 
     * negative value.
     */
    public LabelInfo(String text, int mnemonic) {
        this(text, mnemonic, 0);
        //TODO isn't -1 supposed to be the default value for a mnemonic index?
    }

    /**
     * Creates a new {@code LabelInfo} with the given text, mnemonic character and mnemonic index.
     *
     * @param text The text to be displayed by the label. This may be an empty string but cannot 
     * be null.
     * @param mnemonic The character from the label text that acts as a mnemonic.
     * @param mnemonicIndex The zero-based index of the mnemonic character within the label text. 
     * If the specified label text is an empty string, this property will be ignored and set to -1.
     * 
     * @throws IllegalArgumentException if {@code text} is null, if {@code mnemonic} is a negative
     * value or if {@code mnemonicIndex} is less than -1.
     */
    public LabelInfo(String text, int mnemonic, int mnemonicIndex) {
        Assert.notNull(text);
        this.text = text;
        if (!StringUtils.hasText(text)) {
            mnemonicIndex = -1;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Constructing label info, properties: text='" + text + "', mnemonic=" + mnemonic
                    + ", mnemonicIndex=" + mnemonicIndex);
        }
        Assert.isTrue(mnemonic >= 0 && mnemonicIndex >= -1);
        Assert.isTrue(mnemonicIndex < text.length(), 
                      "The mnemonic index must be less than the text length; mnemonicIndex = "
                      + mnemonicIndex
                      + ", text length = "
                      + text.length());
        
        this.mnemonic = mnemonic;
        this.mnemonicIndex = mnemonicIndex;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + mnemonic;
        result = PRIME * result + mnemonicIndex;
        result = PRIME * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LabelInfo other = (LabelInfo) obj;
        if (mnemonic != other.mnemonic)
            return false;
        if (mnemonicIndex != other.mnemonicIndex)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

    public JLabel configureLabel(JLabel label) {
        Assert.notNull(label);
        label.setText(text);
        label.setDisplayedMnemonic(getMnemonic());
        int index = getMnemonicIndex();
        
        //TODO aren't zero and -1 valid index values?
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
                    logger.debug("Appending colon to text field label text '" + text + "'");
                }
                label.setText(labelText += ":");
            }
        }
        label.setLabelFor(component);
        return label;
    }

    public AbstractButton configureButton(AbstractButton button) {
        //TODO this seems to be implemented in CommandButtonLabelInfo as well
        Assert.notNull(button);
        button.setText(text);
        button.setMnemonic(getMnemonic());
        int index = getMnemonicIndex();
        if (index > 0) {
            button.setDisplayedMnemonicIndex(index);
        }
        return button;
    }

    /**
     * Returns the text to be displayed by the label.
     * @return The label text, possibly an empty string but never null.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the character that is to be
     *
     * @return
     */
    public int getMnemonic() {
        return mnemonic;
    }

    /**
     * Returns the index within the label text of the mnemonic character.
     * @return The index of the mnemonic character.
     */
    public int getMnemonicIndex() {
        return mnemonicIndex;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringCreator(this)
                .append("text", this.text)
                .append("mnemonic", this.mnemonic)
                .append("mnemonicIndex", this.mnemonicIndex)
                .toString();
    }
    
}
