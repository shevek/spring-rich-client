package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.richclient.components.TimeTextField;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

@SuppressWarnings("unchecked")
public final class TimeBinding extends CustomBinding implements PropertyChangeListener
{

    private final TimeTextField field;

    public TimeBinding(FormModel model, String path, Class requiredSourceClass, TimeTextField field)
    {
        super(model, path, requiredSourceClass);
        this.field = field;
    }

    protected void valueModelChanged(Object newValue)
    {
        field.setValue(newValue);
        readOnlyChanged();
    }

    protected JComponent doBindControl()
    {
        field.setValue(getValue());
        field.addPropertyChangeListener("value", this);
        return field;
    }

    public void propertyChange(PropertyChangeEvent evt)
    {
        controlValueChanged(field.getValue());
    }

    protected void readOnlyChanged()
    {
        field.setEditable(isEnabled() && !isReadOnly());
    }

    protected void enabledChanged()
    {
        field.setEnabled(isEnabled());
        readOnlyChanged();
    }
}

