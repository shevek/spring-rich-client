package org.springframework.richclient.form.binding.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;

/**
 * Radio button binder for enum values
 * @author ldo
 */
public class EnumRadioButtonBinder extends AbstractBinder
{

    private boolean nullable = false;
    private boolean readOnly = false;

    /**
     * Creates a new binder
     */
    public EnumRadioButtonBinder()
    {
        super(Enum.class);
    }

    @Override
    protected JComponent createControl(Map context)
    {
        return new JPanel();
    }

    /**
     * Sets whether this control can contain a <code>null</code> value
     * @param nullable <code>true</code> if the binder needs to contain a <code>null</code> value
     */
    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    /**
     * Sets whether this control is read-only
     * @param readOnly <code>true</code> if the binder is read-only
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @Override
    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context)
    {
        EnumRadioButtonBinding binding = new EnumRadioButtonBinding((JPanel) control, formModel,
                formPropertyPath, getPropertyType(formModel, formPropertyPath), getSelectableEnumsList(
                        formModel, formPropertyPath));
        binding.setReadOnly(readOnly);
        return binding;

    }

    private List<Enum> getSelectableEnumsList(FormModel formModel, String formPropertyPath)
    {
        List<Enum> out = new ArrayList<Enum>();
        if (nullable)
        {
            out.add(null);
        }
        for (Enum e : ((Class<Enum>) getPropertyType(formModel, formPropertyPath)).getEnumConstants())
        {
            out.add(e);
        }
        return out;
    }

}