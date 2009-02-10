package org.springframework.richclient.samples.dataeditor.ui;

import org.springframework.richclient.samples.dataeditor.domain.Item;
import org.springframework.richclient.widget.editor.DefaultDataEditorWidget;
import org.springframework.richclient.widget.table.PropertyColumnTableDescription;

public class ItemDataEditor extends DefaultDataEditorWidget
{
    public ItemDataEditor(ItemDataProvider itemDataProvider)
    {
        super("itemDataEditor", itemDataProvider);
        setDetailForm(new ItemForm());
        setFilterForm(new ItemFilterForm());

        PropertyColumnTableDescription tableDescription = new PropertyColumnTableDescription("itemDataEditor", Item.class);
        tableDescription.addPropertyColumn("name");
        tableDescription.addPropertyColumn("description");
        tableDescription.addPropertyColumn("supplier.name");
        setTableWidget(tableDescription);
    }


}
