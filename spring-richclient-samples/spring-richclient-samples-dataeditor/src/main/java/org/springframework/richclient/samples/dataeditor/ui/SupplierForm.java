package org.springframework.richclient.samples.dataeditor.ui;

import com.jgoodies.forms.layout.FormLayout;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.TabbedForm;
import org.springframework.richclient.form.builder.FormLayoutFormBuilder;
import org.springframework.richclient.samples.dataeditor.domain.Supplier;

public class SupplierForm extends TabbedForm
{
    public SupplierForm()
    {
        super(FormModelHelper.createFormModel(new Supplier(), "supplierForm"));
    }

    protected Tab[] getTabs()
    {
        FormLayout layout = new FormLayout("default, 3dlu, fill:pref:nogrow", "default");
        FormLayoutFormBuilder builder = new FormLayoutFormBuilder(getBindingFactory(), layout);
        setFocusControl(builder.addPropertyAndLabel("name")[1]);
        builder.nextRow();
        builder.addPropertyAndLabel("contactName");

        return new Tab[] { new Tab("detail", builder.getPanel())};
    }
}
