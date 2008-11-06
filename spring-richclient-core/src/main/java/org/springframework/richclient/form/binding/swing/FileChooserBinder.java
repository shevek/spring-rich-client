package org.springframework.richclient.form.binding.swing;

import org.springframework.richclient.form.binding.Binder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.components.FileChooser;
import org.springframework.binding.form.FormModel;
import org.springframework.util.Assert;

import javax.swing.*;
import java.util.Map;

public class FileChooserBinder implements Binder
{
    public static final String BINDING_CLIENT_PROPERTY_KEY = "binding";

    private boolean useFile = false;

    private FileChooser.FileChooserMode mode = FileChooser.FileChooserMode.FILE;

    public FileChooserBinder()
    {
    }

    @SuppressWarnings("unchecked")
    protected JComponent createControl(Map context)
    {
        return new FileChooser();
    }

    @SuppressWarnings("unchecked")
    protected Binding doBind(JComponent control, FormModel formModel,
                             String formPropertyPath, Map context)
    {
        final FileChooser chooser = (FileChooser) control;
        if (useFile)
        {
            return new FileChooserBinding(formModel, formPropertyPath,
                    java.io.File.class, chooser, mode, this.useFile);
        }
        else
        {
            return new FileChooserBinding(formModel, formPropertyPath,
                    String.class, chooser, mode, this.useFile);
        }
    }

    @SuppressWarnings("unchecked")
    public Binding bind(FormModel formModel, String formPropertyPath,
                        Map context)
    {
        JComponent control = createControl(context);
        Assert.notNull(control,
                "This binder does not support creating a default control.");
        return bind(control, formModel, formPropertyPath, context);
    }

    @SuppressWarnings("unchecked")
    public Binding bind(JComponent control, FormModel formModel,
                        String formPropertyPath, Map context)
    {
        Binding binding = (Binding) control
                .getClientProperty(BINDING_CLIENT_PROPERTY_KEY);
        if (binding != null)
        {
            throw new IllegalStateException(
                    "Component is already bound to property: "
                            + binding.getProperty());
        }
        binding = doBind(control, formModel, formPropertyPath, context);
        control.putClientProperty(BINDING_CLIENT_PROPERTY_KEY, binding);
        return binding;
    }

    /**
     * @param useFile <code>true</code> when used with {@link java.io.File}, <code>false</code> when used with {@link
     *                java.lang.String}
     */
    public void setUseFile(boolean useFile)
    {
        this.useFile = useFile;
    }

    /** @return <code>true</code> when the binder uses {@link java.io.File}, otherwise false; */
    public boolean isUseFile()
    {
        return useFile;
    }

    /**
     * @param mode Mode in which the control is to be used: <br/> <ul> <li>FileChooserMode.FILE: choose files</li>
     *             <li>FileChooserMode.FOLDER: choose folders</li> </ul>
     */
    public void setMode(FileChooser.FileChooserMode mode)
    {
        this.mode = mode;
    }

    /** @return The filechooser mode */
    public FileChooser.FileChooserMode getMode()
    {
        return mode;
    }

    protected Class<?> getPropertyType(FormModel formModel, String formPropertyPath)
    {
        return formModel.getFieldMetadata(formPropertyPath).getPropertyType();
    }
}
