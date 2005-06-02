/*
 * Copyright 12/04/2005 (C) Our Community Pty. Ltd. All Rights Reserved.
 *
 * $Id$
 */
package org.springframework.binding.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of PropertyChangeListener that logs all 
 * events received. Intended to be used in unit tests.
 * 
 * @author Oliver Hutchison
 */
public class TestPropertyChangeListener implements PropertyChangeListener {

    private String onlyForProperty;

    private List eventsRecevied = new ArrayList();
    
    public TestPropertyChangeListener(String onlyForProperty) {
        this.onlyForProperty = onlyForProperty;
    }

    public void reset() {
        eventsRecevied.clear();
    }

    public List getEventsRecevied() {
        return eventsRecevied;
    }
    
    public int eventCount() {
        return eventsRecevied.size();
    }
    
    public PropertyChangeEvent lastEvent() {
        return (PropertyChangeEvent) eventsRecevied.get(eventCount()-1);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (!onlyForProperty.equals(e.getPropertyName())) {
            throw new UnsupportedOperationException("Received PropertyChangeEvent for unexpected property '"
                    + e.getPropertyName() + "'");
        }
        eventsRecevied.add(e);
    }
}
