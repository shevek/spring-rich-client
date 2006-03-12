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
package org.springframework.richclient.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.validation.ValidationMessage;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.validation.ValidationResultsModel;
import org.springframework.richclient.core.Guarded;
import org.springframework.richclient.core.Message;
import org.springframework.richclient.dialog.Messagable;
import org.springframework.util.Assert;

/**
 * An implementation of ValidationResultsReporter that reports only a single message from
 * the configured validation results models. If there are any errors reported on this or
 * any child's model, then the Guarded object will be disabled and the associated message
 * receiver will be given the newest message posted on the results model.
 * 
 * @author Keith Donald
 */
public class SimpleValidationResultsReporter implements ValidationResultsReporter {
    private static final Log logger = LogFactory.getLog( SimpleValidationResultsReporter.class );

    private ValidationResultsModel resultsModel;

    private Guarded guarded;

    private Messagable messageReceiver;

    private List _children = new ArrayList();

    private ValidationResultsReporter _parent = null;

    /**
     * Constructor.
     * @param resultsModel Validation results model to monitor and report on
     * @param guarded The Guarded object to control
     * @param messageReceiver The receiver for validation messages
     */
    public SimpleValidationResultsReporter(ValidationResultsModel resultsModel, Guarded guarded,
            Messagable messageReceiver) {
        Assert.notNull( resultsModel, "resultsModel is required" );
        Assert.notNull( guarded, "guarded is required" );
        Assert.notNull( messageReceiver, "messagePane is required" );
        this.resultsModel = resultsModel;
        this.guarded = guarded;
        this.messageReceiver = messageReceiver;
        init();
    }

    private void init() {
        resultsModel.addValidationListener( this );

        // Update state based on current results model
        validationResultsChanged( null );
    }

    public void clearErrors() {
        messageReceiver.setMessage( null );
        guarded.setEnabled( true );
    }

    /**
     * Handle a change in the validation results model. Update the guarded object and
     * message receiver based on our current results model state.
     */
    public void validationResultsChanged(ValidationResults results) {
        // If our model is clean, then we need to see if any of our children have errors.
        // If not, then we have our parent update since we may have siblings that need to
        // report there status.

        if( !resultsModel.getHasErrors() ) {
            if( logger.isDebugEnabled() ) {
                logger.debug( "Form has no errors to report; checking children." );
            }

            boolean clean = true;

            // Check children
            for( int i = 0, count = _children.size(); i < count; i++ ) {
                ValidationResultsReporter child = (ValidationResultsReporter) _children.get( i );
                if( child.hasErrors() ) {
                    clean = false;
                    child.validationResultsChanged( null ); // Force it to re-report
                    break;
                }
            }

            // If we aren't clean, then the guarded and message receiver will already have
            // been updated so there's nothing more to do. If we are clean, then we
            // either hand off to our parent, or if there's no parent, then we can finally
            // enable the guard and clear the message.

            if( clean ) {
                // If we have a parent, then we will leave the handling of the guarded and
                // message receiver to it since it may have grand-parents, etc.
                if( _parent != null ) {
                    if( logger.isDebugEnabled() ) {
                        logger.debug( "Form and children are clean, handing off to parent." );
                    }
                    _parent.validationResultsChanged( null );
                } else {
                    if( logger.isDebugEnabled() ) {
                        logger.debug( "Reporters are all clean; enabling guarded component." );
                    }
                    messageReceiver.setMessage( null );
                    guarded.setEnabled( true );
                }
            }
        } else {
            if( logger.isDebugEnabled() ) {
                logger.debug( "Form has errors; disabling guarded component and setting error message." );
            }
            guarded.setEnabled( false );
            if( resultsModel.getMessageCount() > 0 ) {
                ValidationMessage message = getNewestMessage( resultsModel );
                messageReceiver.setMessage( new Message( message.getMessage(), message.getSeverity() ) );
            } else {
                messageReceiver.setMessage( null );
            }
        }
    }

    protected ValidationMessage getNewestMessage(ValidationResults results) {
        ValidationMessage newestMessage = null;
        for( Iterator i = results.getMessages().iterator(); i.hasNext(); ) {
            ValidationMessage message = (ValidationMessage) i.next();
            if( newestMessage == null || newestMessage.getTimeStamp() < message.getTimeStamp() ) {
                newestMessage = message;
            }
        }
        return newestMessage;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.ValidationResultsReporter#hasErrors()
     */
    public boolean hasErrors() {
        boolean errors = resultsModel.getHasErrors();

        for( int i = 0, count = _children.size(); i < count && !errors; i++ ) {
            ValidationResultsReporter child = (ValidationResultsReporter) _children.get( i );
            if( child.hasErrors() ) {
                errors = true;
                break;
            }
        }

        return errors;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.ValidationResultsReporter#addChild(org.springframework.richclient.form.ValidationResultsReporter)
     */
    public void addChild(ValidationResultsReporter child) {
        _children.add( child );
        child.setParent( this );
        validationResultsChanged( null ); // Force a re-reporting
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.ValidationResultsReporter#getParent()
     */
    public ValidationResultsReporter getParent() {
        return _parent;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.ValidationResultsReporter#setParent(org.springframework.richclient.form.ValidationResultsReporter)
     */
    public void setParent(ValidationResultsReporter parent) {
        _parent = parent;
    }

}
