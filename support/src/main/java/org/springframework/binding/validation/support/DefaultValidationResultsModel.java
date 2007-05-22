/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.binding.validation.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.binding.validation.Severity;
import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.core.style.ToStringCreator;
import org.springframework.richclient.util.Assert;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.CachingMapDecorator;
import org.springframework.util.ObjectUtils;

/**
 * Default implementation of ValidationResultsModel
 * 
 * @author  Oliver Hutchison
 */
public class DefaultValidationResultsModel implements ValidationResultsModel, ValidationListener {

    private final EventListenerListHelper validationListeners = new EventListenerListHelper(ValidationListener.class);

    private final CachingMapDecorator propertyValidationListeners = new CachingMapDecorator() {

        protected Object create(Object propertyName) {
            return new EventListenerListHelper(ValidationListener.class);
        }
    };

    private final CachingMapDecorator propertyChangeListeners = new CachingMapDecorator() {

        protected Object create(Object propertyName) {
            return new EventListenerListHelper(PropertyChangeListener.class);
        }
    };

    private final ValidationResultsModel delegateFor;
    
    private List children = new ArrayList();

    private ValidationResults validationResults = EmptyValidationResults.INSTANCE;

    public DefaultValidationResultsModel() {
        delegateFor = this;
    }

    public DefaultValidationResultsModel(ValidationResultsModel delegateFor) {
        this.delegateFor = delegateFor;
    }

    public void updateValidationResults(ValidationResults newValidationResults) {
        Assert.required(newValidationResults, "newValidationResults");
        ValidationResults oldValidationResults = validationResults;
        validationResults = newValidationResults;
        if (oldValidationResults.getMessageCount() == 0 && validationResults.getMessageCount() == 0) {
            return;
        }
        for (Iterator i = propertyValidationListeners.keySet().iterator(); i.hasNext();) {
            String propertyName = (String)i.next();
            if (oldValidationResults.getMessageCount(propertyName) > 0
                    || validationResults.getMessageCount(propertyName) > 0) {
                fireValidationResultsChanged(propertyName);
            }
        }
        fireChangedEvents(oldValidationResults);
    }
    
    // TODO: test
    public void addMessage(ValidationMessage validationMessage) {
        if (!validationResults.getMessages().contains(validationMessage)) {
            ValidationResults oldValidationResults = validationResults;
            List newMessages = new ArrayList(oldValidationResults.getMessages());
            newMessages.add(validationMessage);
            validationResults = new DefaultValidationResults(newMessages);
            fireValidationResultsChanged(validationMessage.getProperty());
            fireChangedEvents(oldValidationResults);
        }
    }

    // TODO: test
    public void removeMessage(ValidationMessage validationMessage) {
        if (validationResults.getMessages().contains(validationMessage)) {
            ValidationResults oldValidationResults = validationResults;
            List newMessages = new ArrayList(oldValidationResults.getMessages());
            newMessages.remove(validationMessage);
            validationResults = new DefaultValidationResults(newMessages);
            fireValidationResultsChanged(validationMessage.getProperty());
            fireChangedEvents(oldValidationResults);
        }
    }

    // TODO: test
    public void replaceMessage(ValidationMessage messageToReplace, ValidationMessage replacementMessage) {        
        ValidationResults oldValidationResults = validationResults;
        List newMessages = new ArrayList(oldValidationResults.getMessages());
        final boolean containsMessageToReplace = validationResults.getMessages().contains(messageToReplace);
        if (containsMessageToReplace) {
            newMessages.remove(messageToReplace);
        }
        newMessages.add(replacementMessage);
        validationResults = new DefaultValidationResults(newMessages);
        if (containsMessageToReplace && !ObjectUtils.nullSafeEquals(messageToReplace.getProperty(), replacementMessage.getProperty())) {
            fireValidationResultsChanged(messageToReplace.getProperty());
        }
        fireValidationResultsChanged(replacementMessage.getProperty());
        fireChangedEvents(oldValidationResults);
    }

    public void clearAllValidationResults() {
        updateValidationResults(EmptyValidationResults.INSTANCE);
    }

    /**
     * Check children too.
     */
    public boolean getHasErrors() {
    	if (validationResults.getHasErrors())
    		return true;
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		if (childModel.getHasErrors())
    			return true;
    	}
        return false;
    }

    public boolean getHasInfo() {
    	if (validationResults.getHasInfo())
    		return true;
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		if (childModel.getHasInfo())
    			return true;
    	}
        return false;
    }

    public boolean getHasWarnings() {
    	if (validationResults.getHasWarnings())
    		return true;
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		if (childModel.getHasWarnings())
    			return true;
    	}
        return false;
    }

    public int getMessageCount() {
    	int count = validationResults.getMessageCount();
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		count += childModel.getMessageCount();
    	}
        return count;
    }

    public int getMessageCount(Severity severity) {
    	int count = validationResults.getMessageCount(severity);
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		count += childModel.getMessageCount(severity);
    	}
        return count;
    }

    public int getMessageCount(String propertyName) {
    	int count = validationResults.getMessageCount(propertyName);
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		count += childModel.getMessageCount(propertyName);
    	}
        return count;
    }

    public Set getMessages() {
    	Set messages = new HashSet();
    	messages.addAll(validationResults.getMessages());
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		messages.addAll(childModel.getMessages());
    	}
        return messages;
    }

    public Set getMessages(Severity severity) {
    	Set messages = new HashSet();
    	messages.addAll(validationResults.getMessages(severity));
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		messages.addAll(childModel.getMessages(severity));
    	}
        return messages;
    }

    public Set getMessages(String propertyName) {
    	Set messages = new HashSet();
    	messages.addAll(validationResults.getMessages(propertyName));
    	Iterator childIter = children.iterator();
    	while (childIter.hasNext())
    	{
    		ValidationResultsModel childModel = (ValidationResultsModel)childIter.next();
    		messages.addAll(childModel.getMessages(propertyName));
    	}
        return messages;
    }

    public void addValidationListener(ValidationListener listener) {
        validationListeners.add(listener);
    }

    public void removeValidationListener(ValidationListener listener) {
        validationListeners.remove(listener);
    }

    public void addValidationListener(String propertyName, ValidationListener listener) {
        getValidationListeners(propertyName).add(listener);
    }

    public void removeValidationListener(String propertyName, ValidationListener listener) {
        getValidationListeners(propertyName).remove(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        throw new UnsupportedOperationException("This method is not implemented");
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyChangeListeners(propertyName).add(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPropertyChangeListeners(propertyName).remove(listener);
    }

    protected void fireChangedEvents(ValidationResults oldValidationResults) {
        fireValidationResultsChanged();
        firePropertyChange(HAS_ERRORS_PROPERTY, oldValidationResults.getHasErrors(), getHasErrors());
        firePropertyChange(HAS_WARNINGS_PROPERTY, oldValidationResults.getHasWarnings(), getHasWarnings());
        firePropertyChange(HAS_INFO_PROPERTY, oldValidationResults.getHasInfo(), getHasInfo());
    }

    protected void fireValidationResultsChanged() {
        validationListeners.fire("validationResultsChanged", delegateFor);
    }

    protected void fireValidationResultsChanged(String propertyName) {
        for (Iterator i = getValidationListeners(propertyName).iterator(); i.hasNext();) {
            ((ValidationListener)i.next()).validationResultsChanged(delegateFor);            
        }
    }

    protected EventListenerListHelper getValidationListeners(String propertyName) {
        return ((EventListenerListHelper)propertyValidationListeners.get(propertyName));
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            EventListenerListHelper propertyChangeListeners = getPropertyChangeListeners(propertyName);
            if (propertyChangeListeners.hasListeners()) {
                PropertyChangeEvent event = new PropertyChangeEvent(delegateFor, propertyName,
                        Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
                propertyChangeListeners.fire("propertyChange", event);
            }
        }
    }

    protected EventListenerListHelper getPropertyChangeListeners(String propertyName) {
        return ((EventListenerListHelper)propertyChangeListeners.get(propertyName));
    }
    
    public String toString() {
        return new ToStringCreator(this).append("messages", getMessages()).toString();
    }
    
    
    /**
     * Add a validationResultsModel as a child to this one.
     * 
     * @param validationResultsModel
     */
    public void add(ValidationResultsModel validationResultsModel)
    {
    	children.add(validationResultsModel);
    	validationResultsModel.addValidationListener(this);
    }
    
    /**
     * Remove the given validationResultsModel from the list of children.
     * 
     * @param validationResultsModel
     */
    public void remove(ValidationResultsModel validationResultsModel)
    {
    	if (children.remove(validationResultsModel))
    		validationResultsModel.removeValidationListener(this);
    }
    
    public void validationResultsChanged(ValidationResults results) {
    	fireValidationResultsChanged();
    }
}