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
package org.springframework.richclient.forms;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;

import org.springframework.rules.values.ValueModel;


/**
 * @author oliverh
 */
public class JFormatedTextFieldValueSetter
extends AbstractValueSetter
implements PropertyChangeListener
{
    private JFormattedTextField component;
    
    public JFormatedTextFieldValueSetter(JFormattedTextField component,
        ValueModel valueModel)
    {
        super(valueModel);
        this.component = component;       
        this.component.addPropertyChangeListener("value", this);       
    }
    
    protected void setComponentValue(Object value) {
        component.setValue(value);
    }
	
	public void propertyChange(PropertyChangeEvent e)
    {	    
        componentValueChanged(component.getValue());
	}
}