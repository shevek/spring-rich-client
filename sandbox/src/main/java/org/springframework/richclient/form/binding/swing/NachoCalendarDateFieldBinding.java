/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.form.binding.swing;

import net.sf.nachocalendar.components.DateField;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;

import javax.swing.JComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * Binds a <cod>Date</code> to a NachoCalendar <code>DateField</code>
 * 
 * @author Geoffrey De Smet
 */
public class NachoCalendarDateFieldBinding extends CustomBinding {

    private final DateField dateField;

    private Boolean showOkCancel;

    public NachoCalendarDateFieldBinding(DateField dateField, FormModel formModel, String formPropertyPath) {
        super(formModel, formPropertyPath, Date.class);
        this.dateField = dateField;
    }

    public Boolean getShowOkCancel() {
        return showOkCancel;
    }

    public void setShowOkCancel(Boolean showOkCancel) {
        this.showOkCancel = showOkCancel;
    }

    protected JComponent doBindControl() {
        dateField.setValue((Date) getValue());
        if (showOkCancel != null) {
            dateField.setShowOkCancel(showOkCancel.booleanValue());
        }
        dateField.addPropertyChangeListener("value", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                controlValueChanged(dateField.getValue());
            }
        });
        // TODO: implement ValueCommitPolicies (copied from TextComponentBinding)
        // new AsYouTypeTextComponentAdapter(textComponent, valueModel);
        return dateField;
    }

    protected void valueModelChanged(Object newValue) {
        dateField.setValue((Date) newValue);
    }

    protected void readOnlyChanged() {
        dateField.setEnabled(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged() {
        dateField.setEnabled(isEnabled() && !isReadOnly());
    }
    
}