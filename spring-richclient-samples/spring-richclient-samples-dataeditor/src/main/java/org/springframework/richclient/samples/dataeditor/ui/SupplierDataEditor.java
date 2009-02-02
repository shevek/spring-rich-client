package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.richclient.samples.dataeditor.domain.Supplier;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.richclient.widget.table.PropertyColumnTableDescription;

public class SupplierDataEditor extends DefaultDataEditorWidget
{
    private static final PropertyColumnTableDescription TABLE_DESCRIPTION;

    static
    {
        TABLE_DESCRIPTION = new PropertyColumnTableDescription("supplierDataEditor", Supplier.class);
        TABLE_DESCRIPTION.addPropertyColumn("name");
        TABLE_DESCRIPTION.addPropertyColumn("contactName");
    }

    public SupplierDataEditor(SupplierDataProvider supplierDataProvider)
    {
         super("supplierDataEditor", supplierDataProvider, new SupplierForm(), TABLE_DESCRIPTION, new SupplierFilterForm());
    }
}
