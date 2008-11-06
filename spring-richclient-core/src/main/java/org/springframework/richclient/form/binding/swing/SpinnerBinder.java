package org.springframework.richclient.form.binding.swing;

import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.binding.form.FormModel;

import javax.swing.*;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SpinnerBinder extends AbstractBinder
{
    private Comparable minimum = null;
    private Comparable maximum = null;
    private Number currentValue = new Integer(0);
    private Number stepValue = new Integer(1);

    public SpinnerBinder()
    {
        this(Integer.class);
    }

    public SpinnerBinder(Class requiredClass)
    {
        super(requiredClass);
    }

    public void setMinimum(Comparable minimum)
    {
        this.minimum = minimum;
    }

    public void setMaximum(Comparable maximum)
    {
        this.maximum = maximum;
    }

    public void setCurrentValue(Number currentValue)
    {
        this.currentValue = currentValue;
    }

    public void setStepValue(Number stepValue)
    {
        this.stepValue = stepValue;
    }

    @Override
    protected JComponent createControl(Map context)
    {
        return new JSpinner(new SpinnerNumberModel(this.currentValue, this.minimum, this.maximum, this.stepValue));
    }

    @Override
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context)
    {
        return new SpinnerBinding(formModel, formPropertyPath, getRequiredSourceClass(), (JSpinner) control);
    }

}
