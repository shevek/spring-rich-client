/*
 * $Header$
 * $Revision$
 * $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.forms;

import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author Keith Donald
 */
public class FormatterFactory extends AbstractFormatterFactory {

    private static final Log logger = LogFactory.getLog(FormatterFactory.class);
    
    private ValueCommitPolicy valueCommitPolicy = ValueCommitPolicy.AS_YOU_TYPE;

    public FormatterFactory() {
        
    }
    
    public FormatterFactory(ValueCommitPolicy policy) {
        setValueCommitPolicy(policy);
    }
    
    public void setValueCommitPolicy(ValueCommitPolicy policy) {
        Assert.notNull(policy);
        this.valueCommitPolicy = policy;
    }

    public AbstractFormatter getFormatter(JFormattedTextField source) {
        Object value = source.getValue();
        DefaultFormatter formatter;
        if (value instanceof Date) {
            formatter = new DateFormatter();
        }
        else if (value instanceof Number) {
            formatter = new NumberFormatter();
        }
        else {
            formatter = new DefaultFormatter();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Factory returning new formatter " + formatter);
        }
        valueCommitPolicy.configure(source, formatter);
        return formatter;
    }
}