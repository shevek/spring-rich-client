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
package org.springframework.richclient.form.binding.swing;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DateField;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.util.Assert;

import javax.swing.JComponent;
import javax.swing.text.DateFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Binds a <cod>Date</code> to a NachoCalendar <code>DateField</code>
 *
 * @author Geoffrey De Smet
 * @author Benoit Xhenseval (added dateFormat setting)
 */
public class NachoCalendarDateFieldBinder extends AbstractBinder implements Binder {

    public static final String SHOW_OK_CANCEL_KEY = "showOkCancel";
    
    private String dateFormat;
    
    public NachoCalendarDateFieldBinder() {
        this(new String[] {SHOW_OK_CANCEL_KEY});
    }

    public NachoCalendarDateFieldBinder(String[] supportedContextKeys) {
        super(Date.class, supportedContextKeys);
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof DateField, "Control must be an instance of DateField.");
        NachoCalendarDateFieldBinding binding = new NachoCalendarDateFieldBinding((DateField) control, formModel, formPropertyPath);
        applyContext(binding, context);
        return binding;
    }

    private void applyContext(NachoCalendarDateFieldBinding binding, Map context) {
        if (context.containsKey(SHOW_OK_CANCEL_KEY)) {
            binding.setShowOkCancel((Boolean) context.get(SHOW_OK_CANCEL_KEY));
        }
    }

    protected JComponent createControl(Map context) {
        DateField dateField;
        if (dateFormat != null) {
            dateField = new DateField(new DateFormatter(new SimpleDateFormat(dateFormat)));
            dateField.setRenderer(new DefaultDayRenderer());
            dateField.setHeaderRenderer(new DefaultHeaderRenderer());
        } else {
            dateField = CalendarFactory.createDateField();
        }
        return dateField;
    }

    /**
     * @return Returns the dateFormat.
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat The dateFormat to set.
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}