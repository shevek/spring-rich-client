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
package org.springframework.richclient.core;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import junit.framework.Assert;
import junit.framework.TestCase;


/**
 * Provides a suite of unit tests for the {@link LabelInfo} class.
 * 
 * @author Peter De Bruycker
 * @author Kevin Stembridge
 */
public class LabelInfoTests extends TestCase {
    
    private static final int DEFAULT_MNEMONIC_INDEX = -1;
    
    private Map invalidLabelDescriptors;
    
    /**
     * Creates a new {@code LabelInfoTests}.
     */
    public LabelInfoTests() {
        this.invalidLabelDescriptors = new HashMap();
        this.invalidLabelDescriptors.put("&", "A mnemonic indicator must be followed by a character.");
        this.invalidLabelDescriptors.put("\\", "A backslash must be followed by an escapable character.");
        this.invalidLabelDescriptors.put("abcd& abcd", "A space character is not a valid mnemonic character.");
        this.invalidLabelDescriptors.put("&ab&cd", "A label descriptor can only contain a single non-escaped &");
        this.invalidLabelDescriptors.put("abcd&", "A mnemonic indicator must be followed by a character.");
        this.invalidLabelDescriptors.put("\\abcd", "Only an ampersand or a backslash can be escaped.");
    }
    
    /**
     * Confirms that null or empty input will result in a LabelInfo instance with a blank 
     * text label and no specified mnemonic.
     */
    public void testForNullOrEmptyInput() {

        LabelInfo info = LabelInfo.valueOf(null);

        Assert.assertEquals("", info.getText());
        Assert.assertEquals(0, info.getMnemonic());
        Assert.assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());
        
        info = LabelInfo.valueOf("");

        Assert.assertEquals("", info.getText());
        Assert.assertEquals(0, info.getMnemonic());
        Assert.assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());
        
    }

    /**
     * Confirms that a label descriptor with various special characters produces a LabelInfo 
     * with expected values for mnemonic and mnemonicIndex.
     */
    public void testValueOfWithValidSyntax() {
        LabelInfo info = LabelInfo.valueOf("Save As");

        Assert.assertEquals("Save As", info.getText());
        Assert.assertEquals(0, info.getMnemonic());
        Assert.assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());
        
        info = LabelInfo.valueOf("S\\&ave @&as");
        Assert.assertEquals("S&ave @as", info.getText());
        Assert.assertEquals(KeyEvent.VK_A, info.getMnemonic());
        Assert.assertEquals(7, info.getMnemonicIndex());
        
    }
    
    /**
     * Confirms that exceptions are thrown for label descriptors that violate the syntax rules.
     */
    public void testInvalidSyntax() {
        
        Iterator entryIterator = this.invalidLabelDescriptors.entrySet().iterator();
        
        while (entryIterator.hasNext()) {
            
            Map.Entry entry = (Map.Entry) entryIterator.next();
            
            try {
                LabelInfo.valueOf((String) entry.getKey());
                Assert.fail("Should have thrown an IllegalArgumentException for label descriptor [" 
                            + entry.getKey()
                            + "] due to "
                            + entry.getValue());
            }
            catch (IllegalArgumentException e) {
                //do nothing, test succeeded
            }
            
        }
        
    }
    
    /**
     * Confirms that any ampersands escaped with a backslash character appear as text in the label.
     *
     */
    public void testForEscapedAmpersands() {
        
        LabelInfo info = LabelInfo.valueOf("&Save \\& Run");
        
        Assert.assertEquals(0, info.getMnemonicIndex());
        Assert.assertEquals(KeyEvent.VK_S, info.getMnemonic());
        Assert.assertEquals("Save & Run", info.getText());
        
    }
    
    /**
     * Confirms that any backslashes escaped with a backslash character appear as text in the label.
     *
     */
    public void testForEscapedBackslashes() {
        
        LabelInfo info = LabelInfo.valueOf("This is a backslash (\\\\)");
        Assert.assertEquals("This is a backslash (\\)", info.getText());
        
    }
    
    /**
     * Confirms that any @ symbols, used by the CommandButtonLabelInfo, will not be given special 
     * treatment by a LabelInfo.
     */
    public void testForAtSymbols() {
        
        LabelInfo info = LabelInfo.valueOf("Something with an @ in it");
        Assert.assertEquals("Something with an @ in it", info.getText());
        info = LabelInfo.valueOf("S\\&ave with an @ &as");
        Assert.assertEquals("S&ave with an @ as", info.getText());
        Assert.assertEquals(KeyEvent.VK_A, info.getMnemonic());
        Assert.assertEquals(16, info.getMnemonicIndex());
        
    }
    
    public void testConstructor() {
        LabelInfo info = new LabelInfo("test");
        assertEquals("test", info.getText());
        assertEquals(0, info.getMnemonic());
        assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());

        info = new LabelInfo("test", KeyEvent.VK_T);
        assertEquals("test", info.getText());
        assertEquals(KeyEvent.VK_T, info.getMnemonic());
        assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());

        info = new LabelInfo("test", KeyEvent.VK_T, 3);
        assertEquals("test", info.getText());
        assertEquals(KeyEvent.VK_T, info.getMnemonic());
        assertEquals(3, info.getMnemonicIndex());
    }
    
    public void testEquals() throws Exception {
        LabelInfo info1 = new LabelInfo("test", 0, 0);
        LabelInfo info2 = new LabelInfo("test", 0, 0);
        assertTrue(info1.equals(info2));
        info2 = new LabelInfo("test", 1, 0);
        assertFalse(info1.equals(info2));
        info2 = new LabelInfo("test", 0, 1);
        assertFalse(info1.equals(info2));
        info2 = new LabelInfo("test2", 0, 0);
        assertFalse(info1.equals(info2));
        assertFalse(info1.equals(null));
    }

    public void testHashCode() throws Exception {
        LabelInfo info1 = new LabelInfo("test", 0, 0);
        LabelInfo info2 = new LabelInfo("test", 0, 0);
        assertTrue(info1.hashCode() == info2.hashCode());
        info2 = new LabelInfo("test", 1, 0);
        assertFalse(info1.hashCode() == info2.hashCode());
        LabelInfo info3 = new LabelInfo("test", 0, 1);
        assertFalse(info1.hashCode() == info2.hashCode());
        assertFalse(info2.hashCode() == info3.hashCode());
        info2 = new LabelInfo("test2", 0, 0);
        assertFalse(info1.hashCode() == info2.hashCode());
    }

    public void testConstructorEmptyText() {
        LabelInfo info = new LabelInfo("", KeyEvent.VK_A, -1);
        assertEquals("", info.getText());
        assertEquals(KeyEvent.VK_A, info.getMnemonic());
        assertEquals(DEFAULT_MNEMONIC_INDEX, info.getMnemonicIndex());
    }

    public void testConstructorNullText() {
        try {
            new LabelInfo(null);
            fail("no null text");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConstructorNegativeMnemonic() {
        try {
            new LabelInfo("test", -5);
            fail("No negative mnemonics");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConfigureLabel() {
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Save As", 'A', 5);
        info.configureLabel(label);

        assertEquals("Save As", label.getText());
        assertEquals('A', label.getDisplayedMnemonic());
        assertEquals(5, label.getDisplayedMnemonicIndex());
    }

    public void testConfigureLabelNull() {
        LabelInfo info = new LabelInfo("Test");
        
        try {
            info.configureLabel(null);
            Assert.fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            //do nothing, test succeeded
        }
        
    }

    public void testConfigureLabelFor() {
        JTextField field = new JTextField();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name", 'N');
        info.configureLabelFor(label, field);

        assertEquals("LabelInfo must add colon if none present", "Name:", label.getText());
        assertEquals('N', label.getDisplayedMnemonic());
        assertEquals(field, label.getLabelFor());
    }

    public void testConfigureLabelForWithColon() {
        JTextField field = new JTextField();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name:", KeyEvent.VK_N);
        info.configureLabelFor(label, field);

        assertEquals("Name:", label.getText());
        assertEquals(KeyEvent.VK_N, label.getDisplayedMnemonic());
        assertEquals(field, label.getLabelFor());
    }

    public void testConfigureLabelForJPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        LabelInfo info = new LabelInfo("Name", KeyEvent.VK_N);
        info.configureLabelFor(label, panel);

        assertEquals("No colon for panel", "Name", label.getText());
        assertEquals(KeyEvent.VK_N, label.getDisplayedMnemonic());
        assertEquals(panel, label.getLabelFor());
    }

    public void testConstructorMnemonicIndexGreaterThanLength() {
        try {
            new LabelInfo("test", KeyEvent.VK_T, 4);
            fail("Mnemonic index must be < text.length()");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public void testConstructorNegativeMnemonicIndex() {
        try {
            new LabelInfo("test", KeyEvent.VK_T, -2);
            fail("index must be >= -1");
        }
        catch (IllegalArgumentException e) {
            pass();
        }
    }

    public static final void pass() {
        // test passes
    }
        
}
