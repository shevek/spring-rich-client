package org.springframework.richclient.samples.dataeditor.ui;

import com.jgoodies.forms.layout.FormLayout;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.TabbedForm;
import org.springframework.richclient.form.builder.FormLayoutFormBuilder;
import org.springframework.richclient.samples.dataeditor.domain.Item;

public class ItemForm extends TabbedForm
{
    public ItemForm()
    {
        super(FormModelHelper.createFormModel(new Item(), "itemForm"));
    }

    protected Tab[] getTabs()
    {
        FormLayout layout = new FormLayout("default, 3dlu, fill:pref:nogrow, 3dlu, 100dlu", "default");
        FormLayoutFormBuilder builder = new FormLayoutFormBuilder(getBindingFactory(), layout);
        setFocusControl(builder.addPropertyAndLabel("name")[1]);
        builder.nextRow();
        builder.addPropertyAndLabel("description");
        builder.nextRow();
        builder.addPropertyAndLabel("supplier");//, "supplierBinder");

        return new Tab[] { new Tab("detail", builder.getPanel())};
    }
}
