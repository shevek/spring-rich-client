/*
 * Copyright 2004 (C) Our Community Pty. Ltd. All Rights Reserved
 * 
 * $Id$
 */

package org.springframework.richclient.form.builder.support;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.util.Assert;

/**
 * @author  oliverh
 */
public abstract class AbstractFormComponentInterceptor implements
        FormComponentInterceptor {

    private final FormModel formModel;
    
    protected AbstractFormComponentInterceptor() {
        formModel = null;
    }
    
    protected AbstractFormComponentInterceptor(FormModel formModel) {
        Assert.notNull(formModel);
        this.formModel = formModel;
    }
    
    protected FormModel getFormModel() {
        return formModel;
    }
    
    public JComponent processLabel(String propertyName, JComponent label) {
        return label;
    }

    public JComponent processComponent(String propertyName, JComponent component) {
        return component;
    }
    
    protected JComponent getInnerComponent(JComponent component) {
        if (component instanceof JScrollPane) {
            return (JComponent) ((JScrollPane) component).getViewport().getView();
        }
        return component;
    }
}
