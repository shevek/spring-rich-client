package org.springframework.richclient.form.binding.swing;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.support.CustomBinding;
import org.springframework.richclient.components.FileChooser;

import javax.swing.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

public class FileChooserBinding extends CustomBinding
{
    private final FileChooser field;
    private final boolean useFile;

    public FileChooserBinding(FormModel model, String path, Class<?> class1, FileChooser field,
                              FileChooser.FileChooserMode mode, boolean useFile)
    {
        super(model, path, class1);
        this.field = field;
        this.field.setMode(mode);
        this.useFile = useFile;
    }

    protected void valueModelChanged(Object newValue)
    {
        if (!useFile)
        {
            field.setText((String) newValue);
        }
        else
        {
            field.setText(((java.io.File) newValue).getAbsolutePath());
        }
        readOnlyChanged();
    }

    protected JComponent doBindControl()
    {
        if (!useFile && getValue() != null)
        {
            field.setText((String) getValue());
        }
        else if (useFile && getValue() != null)
        {
            field.setText(((java.io.File) getValue()).getAbsolutePath());
        }
        else
        {
            field.setText("");
        }
        field.addFocusListener(new FocusListener()
        {

            public void focusGained(FocusEvent e)
            {
            }

            public void focusLost(FocusEvent e)
            {
                if (field.isEditable())
                {
                    if (useFile)
                    {
                        controlValueChanged(new java.io.File(field.getText()));
                    }
                    else
                    {
                        controlValueChanged(field.getText());
                    }
                }
            }
        });
        return field;
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
