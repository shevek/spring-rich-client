package org.springframework.richclient.samples.simple.ui;

import com.jgoodies.forms.layout.FormLayout;
import org.springframework.richclient.form.AbstractFocussableForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.FormLayoutFormBuilder;
import org.springframework.richclient.samples.simple.domain.TodoItem;

import javax.swing.*;

public class TodoForm  extends AbstractFocussableForm
{
    public TodoForm()
    {
        super(FormModelHelper.createFormModel(new TodoItem(), "todoItemForm"));
    }

    protected JComponent createFormControl()
    {
        FormLayout layout = new FormLayout("right:pref, 4dlu, default", "default");
        FormLayoutFormBuilder builder = new FormLayoutFormBuilder(getBindingFactory(), layout);

        builder.addPropertyAndLabel("name");
        builder.nextRow();
        builder.addPropertyAndLabel("description");
        builder.nextRow();
        builder.addPropertyAndLabel("todoDate");

        return builder.getPanel();
    }
}
