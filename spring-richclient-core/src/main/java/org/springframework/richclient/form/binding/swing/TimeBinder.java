package org.springframework.richclient.form.binding.swing;

import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.components.TimeTextField;
import org.springframework.binding.form.FormModel;

import javax.swing.*;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TimeBinder extends AbstractBinder
{

    public TimeBinder()
    {
        super(Date.class);
    }

    protected JComponent createControl(Map context)
    {
        return new TimeTextField();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context)
    {
        return new TimeBinding(formModel, formPropertyPath, Date.class, (TimeTextField) control);
    }

}
