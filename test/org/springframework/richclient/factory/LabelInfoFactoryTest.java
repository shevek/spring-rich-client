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

import org.springframework.richclient.command.config.CommandButtonLabelInfo;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class LabelInfoFactoryTest extends TestCase {

    public void testCreateLabelInfo() {
        LabelInfo info = LabelInfoFactory.createLabelInfo("Test");

        assertEquals("Test", info.getText());
        assertEquals(0, info.getMnemonic());
        assertEquals(0, info.getMnemonicIndex());
    }
    
    public void testCreateLabelInfoEncoded() {
        LabelInfo info = LabelInfoFactory.createLabelInfo("Save &as");
        
        assertEquals("Save as", info.getText());
        assertEquals('A', info.getMnemonic());
        assertEquals(5, info.getMnemonicIndex());
    }
    
    public void testCreateButtonLabelInfoNoAccelerator() {
        CommandButtonLabelInfo info = LabelInfoFactory.createButtonLabelInfo("Save &as");
        
        assertEquals("Save as", info.getText());
        assertEquals('A', info.getMnemonic());
        assertEquals(5, info.getMnemonicIndex());
        assertNull(info.getAccelerator());
    }
    
    public void testCreateButtonLabelInfo() {
        CommandButtonLabelInfo info = LabelInfoFactory.createButtonLabelInfo("Save &as@ctrl A");
        
        assertEquals("Save as", info.getText());
        assertEquals('A', info.getMnemonic());
        assertEquals(5, info.getMnemonicIndex());
        assertNotNull("ctrl A is invalid keystroke", info.getAccelerator());
        assertEquals(KeyStroke.getKeyStroke("ctrl A"), info.getAccelerator());
    }
    
    public void testCreateButtonLabelInfoInvalidAccelerator() {
        CommandButtonLabelInfo info = LabelInfoFactory.createButtonLabelInfo("Save &as@Bogus keystroke");
        
        assertEquals("Save as", info.getText());
        assertEquals('A', info.getMnemonic());
        assertEquals(5, info.getMnemonicIndex());
        assertNull(info.getAccelerator());
    }
}
