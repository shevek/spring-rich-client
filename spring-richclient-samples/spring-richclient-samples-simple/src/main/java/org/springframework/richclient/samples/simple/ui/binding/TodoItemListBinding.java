package org.springframework.richclient.samples.simple.ui.binding;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.AbstractGlazedListsBinding;
import org.springframework.richclient.samples.simple.domain.TodoItem;
import org.springframework.richclient.samples.simple.ui.TodoForm;
import org.springframework.richclient.widget.table.PropertyColumnTableDescription;
import org.springframework.richclient.widget.table.TableDescription;

public class TodoItemListBinding extends AbstractGlazedListsBinding
{
    public TodoItemListBinding(FormModel formModel, String formPropertyPath)
    {
        super(formModel, formPropertyPath);
        setDialogId("todoItemListBindingDialog");
        setAddSupported(true);
        setEditSupported(true);
        setRemoveSupported(true);
        setShowDetailSupported(true);
    }

    protected TableDescription getTableDescription()
    {
        PropertyColumnTableDescription desc = new PropertyColumnTableDescription("todoListBinding", TodoItem.class);
        desc.addPropertyColumn("name");
        desc.addPropertyColumn("description");
        return desc;
    }

    // detail form behavior

    @Override
    protected Object getNewFormObject()
    {
        return new TodoItem();
    }

    @Override
    protected AbstractForm createAddEditForm()
    {
        return new TodoForm();
    }
    
    @Override
    protected AbstractForm createDetailForm()
    {
        AbstractForm f = new TodoForm();
        f.getFormModel().setReadOnly(true);
        return f;
    }
}
