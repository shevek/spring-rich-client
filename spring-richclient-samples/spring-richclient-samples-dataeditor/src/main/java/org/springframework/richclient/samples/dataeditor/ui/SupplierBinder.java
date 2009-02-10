package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.swing.editor.AbstractLookupBinder;
import org.springframework.richclient.form.binding.swing.editor.AbstractLookupBinding;
import org.springframework.richclient.samples.dataeditor.domain.Supplier;
import org.springframework.richclient.samples.dataeditor.domain.SupplierFilter;

import java.util.Map;
import java.awt.*;

public class SupplierBinder extends AbstractLookupBinder
{
    public SupplierBinder()
    {
        super("supplierDataEditor");
    }

    protected AbstractLookupBinding getLookupBinding(FormModel formModel, String formPropertyPath, Map context)
    {
        return new AbstractLookupBinding(getDataEditor(), formModel, formPropertyPath)
        {
            public String getObjectLabel(Object o)
            {
                return ((Supplier) o).getName();
            }

            protected Object createFilterFromString(String textFieldValue)
            {
                SupplierFilter s = new SupplierFilter();
                s.setNameContains(textFieldValue);
                return s;
            }

            @Override
            public Dimension getDialogSize()
            {
                return new Dimension(800,600);
            }
        };
    }
}
