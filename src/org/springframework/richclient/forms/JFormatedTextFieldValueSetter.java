/*
 * Copyright 2004 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.richclient.forms;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;

import org.springframework.rules.values.ValueModel;


/**
 * @author oliverh
 * @version $Revision$ $Date$
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